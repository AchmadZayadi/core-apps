package com.sesolutions.responses.store.checkout


import com.google.gson.annotations.SerializedName

data class Order(
        @SerializedName("change_rate")
        val changeRate: Int,
        @SerializedName("cheque_id")
        val chequeId: Int,
        @SerializedName("commission_amount")
        val commissionAmount: String,
        @SerializedName("creation_date")
        val creationDate: String,
        @SerializedName("currency_symbol")
        val currencySymbol: String,
        @SerializedName("gateway_id")
        val gatewayId: String,
        @SerializedName("gateway_transaction_id")
        val gatewayTransactionId: String,
        @SerializedName("gateway_type")
        val gatewayType: String,
        @SerializedName("ip_address")
        val ipAddress: String,
        @SerializedName("item_count")
        val itemCount: Int,
        val menus: List<Menu>,
        @SerializedName("modified_date")
        val modifiedDate: String,
        @SerializedName("order_id")
        val orderId: Int,
        @SerializedName("order_note")
        val orderNote: String,
        @SerializedName("owner_name")
        val ownerName: String,
        @SerializedName("parent_order_id")
        val parentOrderId: Int,
        @SerializedName("shipping_delivery_tile")
        val shippingDeliveryTile: String,
        @SerializedName("shipping_taxes")
        val shippingTaxes: String,
        val status: String,
        @SerializedName("store_id")
        val storeId: Int,
        @SerializedName("store_title")
        val storeTitle: String,
        val total: String,
        @SerializedName("total_admintax_cost")
        val totalAdmintaxCost: String,
        @SerializedName("total_billingtax_cost")
        val totalBillingtaxCost: String,
        @SerializedName("total_shippingtax_cost")
        val totalShippingtaxCost: String,
        @SerializedName("user_id")
        val userId: Int
) {
    data class Menu(
            val label: String,
            val name: String
    )
}