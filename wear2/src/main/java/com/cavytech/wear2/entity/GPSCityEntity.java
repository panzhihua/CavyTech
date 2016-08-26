package com.cavytech.wear2.entity;

/**
 * Created by longjining on 16/4/25.
 */
public class GPSCityEntity {


    /**
     * status : OK
     * result : {"location":{"lng":120.169837,"lat":30.341679},"formatted_address":"浙江省杭州市拱墅区长滨街88号","business":"东新路","addressComponent":{"city":"杭州市","direction":"西南","distance":"71","district":"拱墅区","province":"浙江省","street":"长滨街","street_number":"88号"},"cityCode":179}
     */

    private String status;
    /**
     * location : {"lng":120.169837,"lat":30.341679}
     * formatted_address : 浙江省杭州市拱墅区长滨街88号
     * business : 东新路
     * addressComponent : {"city":"杭州市","direction":"西南","distance":"71","district":"拱墅区","province":"浙江省","street":"长滨街","street_number":"88号"}
     * cityCode : 179
     */

    private ResultBean result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * lng : 120.169837
         * lat : 30.341679
         */

        private LocationBean location;
        private String formatted_address;
        private String business;
        /**
         * city : 杭州市
         * direction : 西南
         * distance : 71
         * district : 拱墅区
         * province : 浙江省
         * street : 长滨街
         * street_number : 88号
         */

        private AddressComponentBean addressComponent;
        private int cityCode;

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }

        public String getBusiness() {
            return business;
        }

        public void setBusiness(String business) {
            this.business = business;
        }

        public AddressComponentBean getAddressComponent() {
            return addressComponent;
        }

        public void setAddressComponent(AddressComponentBean addressComponent) {
            this.addressComponent = addressComponent;
        }

        public int getCityCode() {
            return cityCode;
        }

        public void setCityCode(int cityCode) {
            this.cityCode = cityCode;
        }

        public static class LocationBean {
            private double lng;
            private double lat;

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }
        }

        public static class AddressComponentBean {
            private String city;
            private String direction;
            private String distance;
            private String district;
            private String province;
            private String street;
            private String street_number;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public String getStreet_number() {
                return street_number;
            }

            public void setStreet_number(String street_number) {
                this.street_number = street_number;
            }
        }
    }
}
