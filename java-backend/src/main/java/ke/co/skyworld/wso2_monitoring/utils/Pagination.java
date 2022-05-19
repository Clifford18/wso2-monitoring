package ke.co.skyworld.wso2_monitoring.utils;

import io.undertow.server.HttpServerExchange;

import java.util.Deque;

public class Pagination {

    public static int[] getPageAndPageSize(HttpServerExchange exchange) {
        int[] pageAndPageSize = new int[]{1, 10};

        Deque<String> page = exchange.getQueryParameters().get("page");
        Deque<String> pageSize = exchange.getQueryParameters().get("pageSize");

        if (page != null) {
            try {
                if(Integer.parseInt(page.getFirst()) > 0){
                    pageAndPageSize[0] = Integer.parseInt(page.getFirst());
                }

            } catch (Exception ignore) {

            }
        }

        if (pageSize != null) {
            try {
                if(Integer.parseInt(pageSize.getFirst()) > 0){
                    pageAndPageSize[1] = Integer.parseInt(pageSize.getFirst());
                }

            } catch (Exception ignore) {

            }
        }
        return pageAndPageSize;
    }

}
