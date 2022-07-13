package com.sesolutions.responses.contest;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Packages {
    private int package_id;//2,
    @SerializedName("existing_package_id")
    private int existingPackageId;//2,
    private int item_count;//5,
    private int price;//100,
    private int renew_link_days;//0,
    private int is_renew_link;//0,
    private int enabled;//1,
    // private int default;//0,
    private int order;//2,
    private int highlight;//1,
    private int show_upgrade;//0,
    private String title;//test package",
    private String description;//package description",
    private String custom_fields;//1",
    private String member_level;//,0,",
    private String recurrence;//1",
    private String recurrence_type;//month",
    private String payment_type;//month",
    private String duration;//0",
    private String duration_type;//forever",
    private String custom_fields_params;//[]",
    private String creation_date;//2018-09-17 04:52:01",
    private String modified_date;//2018-09-17 04:52:01",
    private String price_type;//$100.00"
    private List<Options> params;
    @SerializedName("subscribe_detail")
    private List<Options> detail;
    private Map<String, Options> map;

    public String getPaymentType() {
        return payment_type;
    }

    public List<Options> getDetail() {
        return detail;
    }

    public List<Options> getParams() {
        return params;
    }

    public int getPackage_id() {
        return package_id;
    }

    public int getExistingPackageId() {
        return existingPackageId;
    }

    public int getItem_count() {
        return item_count;
    }

    public int getPrice() {
        return price;
    }

    public int getRenew_link_days() {
        return renew_link_days;
    }

    public int getIs_renew_link() {
        return is_renew_link;
    }

    public int getEnabled() {
        return enabled;
    }

    public int getOrder() {
        return order;
    }

    public int getHighlight() {
        return highlight;
    }

    public int getShow_upgrade() {
        return show_upgrade;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCustom_fields() {
        return custom_fields;
    }

    public String getMember_level() {
        return member_level;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public String getRecurrence_type() {
        return recurrence_type;
    }

    public String getDuration() {
        return duration;
    }

    public String getDuration_type() {
        return duration_type;
    }

    public String getCustom_fields_params() {
        return custom_fields_params;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public String getModified_date() {
        return modified_date;
    }

    public String getPrice_type() {
        return price_type;
    }

    public boolean hasSubscribed() {
        if (map == null && null != detail) {
            map = new HashMap<>();
            for (Options opt : detail) {
                map.put(opt.getName(), opt);
            }
        }
        return null != detail;
    }

    public String getMapValue(String key) {
        try {
            return map.get(key).getLabel() + map.get(key).getValue();
        } catch (Exception e) {
            return "";
        }
    }
}
