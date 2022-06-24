package ke.co.skyworld.domain.beans.notifications;

public class ProgressNotificationMessage {
        private double percentage_done;
        private long time_elapsed;
        private String str_time_elapsed;
        private long eta;
        private String str_eta;

        public ProgressNotificationMessage(double percentage_done, long time_elapsed, String str_time_elapsed, long eta,
                                           String str_eta) {
            this.percentage_done = percentage_done;
            this.time_elapsed = time_elapsed;
            this.str_time_elapsed = str_time_elapsed;
            this.eta = eta;
            this.str_eta = str_eta;
        }

        public double getPercentage_done() {
            return percentage_done;
        }

        public long getTime_elapsed() {
            return time_elapsed;
        }

        public String getStr_time_elapsed() {
            return str_time_elapsed;
        }

        public long getEta() {
            return eta;
        }

        public String getStr_eta() {
            return str_eta;
        }
    }