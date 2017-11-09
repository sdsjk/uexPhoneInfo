package com.cloud.adapter.cloud_adaptation;

import java.util.List;

/**
 * Created by zhang on 2017/10/12.
 */

public class PhoneInfo {


    private List<LinksBean> links;

    public List<LinksBean> getLinks() {
        return links;
    }

    public void setLinks(List<LinksBean> links) {
        this.links = links;
    }

    public static class LinksBean {
        /**
         * num : 18511460633
         * name : 张三
         * positioninfo : 航天局局长
         * messageinfo : 公司本部—信息中心-系统运营
         * companyinfo : 北京重工业集团有限公司
         */

        private String num;
        private String name;
        private String positioninfo;
        private String messageinfo;
        private String companyinfo;

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPositioninfo() {
            return positioninfo;
        }

        public void setPositioninfo(String positioninfo) {
            this.positioninfo = positioninfo;
        }

        public String getMessageinfo() {
            return messageinfo;
        }

        public void setMessageinfo(String messageinfo) {
            this.messageinfo = messageinfo;
        }
        public String getCompanyinfo() {
            return companyinfo;
        }

        public void setCompanyinfo(String companyinfo) {
            this.companyinfo = companyinfo;
        }
    }

    @Override
    public String toString() {
        return "PhoneInfo{" +
                "links=" + links +
                '}';
    }
}
