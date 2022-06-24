package ke.co.skyworld.endpoints.handlers.crud;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.Q;
import ke.co.skyworld.repository.Repository;
import ke.co.skyworld.repository.beans.*;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.repository.query.FilterTokenizer;
import ke.co.skyworld.repository.query.QueryBuilder;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.data_formatting.XmlObject;
import ke.co.skyworld.utils.http.ScedarHttpHandler;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;
import ke.co.skyworld.utils.memory.JvmManager;

import java.util.ArrayList;
import java.util.List;

public class Read extends ScedarHttpHandler {
    private String tableName;
    private String internalServerErrorMessage = "Error ";
    private String notFoundErrorMessage = "Not found ";

    private FilterPredicate filterPredicate = null;
    private FlexicoreHashMap queryArguments = null;

    public Read(String tableName) {
        this.tableName = tableName;
    }

    public Read setInternalServerErrorMessage(String internalServerErrorMessage) {
        this.internalServerErrorMessage = internalServerErrorMessage;
        return this;
    }

    public Read setNotFoundErrorMessage(String notFoundErrorMessage) {
        this.notFoundErrorMessage = notFoundErrorMessage;
        return this;
    }

    public Read setFilterPredicate(FilterPredicate filterPredicate) {
        this.filterPredicate = filterPredicate;
        return this;
    }

    public Read setQueryArguments(FlexicoreHashMap queryArguments) {
        this.queryArguments = queryArguments;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Read getBatchRecords(HttpServerExchange exchange) {
        int[] pageAndPageSize = this.getPageAndPageSize(exchange);
        String filterString = getQueryParam(exchange, Constants.FILTER);
        String fields = getQueryParam(exchange, Constants.FIELDS);
        String orderByString = getQueryParam(exchange, Constants.ORDER_BY);
        String fetchStrategy = getQueryParam(exchange, Constants.FETCH_STRATEGY);
        fetchStrategy = (fetchStrategy == null || fetchStrategy.isEmpty()) ? Constants.FETCH_STRATEGY_LAZY : fetchStrategy;
        Column[] filterFields = null;

        //Build Order By Phrase
        try {

            if(orderByString != null && !orderByString.isEmpty()){
                orderByString = orderByString.replaceAll("\\{","").replaceAll("}", "");
            } else {
                orderByString = null;
            }

        } catch (Exception e){
            e.printStackTrace();
            orderByString = null;
        }

        //Populate filterFields
        if(fields != null && !fields.isEmpty()){
            String[] fieldz = fields.split(",");
            filterFields = new Column[fieldz.length];

            for (int i = 0; i < fieldz.length ; i++) {
                filterFields[i] = new Column(fieldz[i]);
            }
        }

        TransactionWrapper<PageableWrapper> twrapper;
        if (filterString != null && !filterString.isEmpty()) {
            TransactionWrapper<Object[]> filterTWrapper =
                    FilterTokenizer.generatePredicate(filterString, tableName);
            if(filterTWrapper.hasErrors())
            {
                this.send(exchange, new ExceptionRepresentation(
                        "Error retrieving records",
                        exchange.getRequestURI(),
                        filterTWrapper.getErrors(),
                        StatusCodes.INTERNAL_SERVER_ERROR,
                        exchange.getRequestMethod()
                ), StatusCodes.INTERNAL_SERVER_ERROR);
                return this;
            }
            Object[] filterObjects = filterTWrapper.getData();
            String additionalFilters = filterObjects[0].toString();

            if(additionalFilters != null && !additionalFilters.isEmpty()){
                filterPredicate = new FilterPredicate( additionalFilters + " and  ("+filterPredicate.getClause()+")");
            }

            queryArguments.putAll((FlexicoreHashMap) filterObjects[1]);
            if(filterFields != null && filterFields.length > 0){
                if(orderByString != null){
                    twrapper = Repository.selectWhereOrderBy(tableName, filterFields, filterPredicate, orderByString, queryArguments, fetchStrategy, pageAndPageSize);
                } else {
                    twrapper = Repository.selectWhere(tableName, filterFields, filterPredicate, queryArguments, fetchStrategy, pageAndPageSize);
                }
            } else {
                twrapper = Repository.selectWhere(tableName, filterPredicate, queryArguments, fetchStrategy, pageAndPageSize);
            }

        } else if(fields != null && !fields.isEmpty()){
            if (filterPredicate == null || queryArguments == null){
                if(orderByString != null){
                    twrapper = Repository.selectOrderBy(tableName, filterFields, orderByString, fetchStrategy, pageAndPageSize);
                } else {
                    twrapper = Repository.select(tableName, filterFields, fetchStrategy, pageAndPageSize);
                }
            } else {
                if(orderByString != null){
                    twrapper = Repository.selectWhereOrderBy(tableName, filterFields, filterPredicate, orderByString, queryArguments, fetchStrategy, pageAndPageSize);
                } else {
                    twrapper = Repository.selectWhere(tableName, filterFields, filterPredicate, queryArguments, fetchStrategy, pageAndPageSize);
                }
            }
        } else {
            if (filterPredicate == null || queryArguments == null){
                if(orderByString != null){
                    twrapper = Repository.selectOrderBy(tableName, orderByString, fetchStrategy, pageAndPageSize);
                } else {
                    twrapper = Repository.selectWithFetchStrategy(tableName, fetchStrategy, pageAndPageSize);
                }
            } else {
                if(orderByString != null){
                    twrapper = Repository.selectWhereOrderBy(tableName, filterPredicate, orderByString, queryArguments, fetchStrategy, pageAndPageSize);
                } else {
                    twrapper = Repository.selectWhere(tableName, filterPredicate, queryArguments, fetchStrategy, pageAndPageSize);
                }
            }
        }

        PageableWrapper<FlexicoreArrayList> pageableWrapper = twrapper.getData();

        if (twrapper.hasErrors()) {
            send(exchange, new ExceptionRepresentation(
                    internalServerErrorMessage,
                    exchange.getRequestURI(),
                    twrapper.getErrors(),
                    StatusCodes.INTERNAL_SERVER_ERROR,
                    exchange.getRequestMethod()
            ), StatusCodes.INTERNAL_SERVER_ERROR);
            return this;
        }
        FlexicoreArrayList flexicoreArrayList = pageableWrapper.getRecords();
        if (flexicoreArrayList == null) {
            send(exchange, new ExceptionRepresentation(
                    notFoundErrorMessage,
                    exchange.getRequestURI(),
                    notFoundErrorMessage,
                    StatusCodes.NOT_FOUND,
                    exchange.getRequestMethod()
            ), StatusCodes.NOT_FOUND);
            return this;
        }

        if(filterFields != null && filterFields.length > 0) {
            String extractedFilterFields = filterFields[0].getColumnName();
            if(extractedFilterFields != null && !extractedFilterFields.equals("*")){
                this.send(exchange, pageableWrapper, StatusCodes.OK);
                return this;
            }
        }

        if(fetchStrategy.equals(Constants.FETCH_STRATEGY_COUNT)){
            this.send(exchange, pageableWrapper, StatusCodes.OK);
            return this;
        }

        this.send(exchange, pageableWrapper, StatusCodes.OK);
        return this;
    }
}
