package com.sesolutions.responses.Courses


import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.ErrorResponse

data class ViewCourseOrder(
        val result: Result,
        @SerializedName("session_id")
        val sessionId: String) : ErrorResponse() {
    data class Result(
            val billing_address: List<Billing>,
            @SerializedName("billing_name")
            val billingName: String,
            val footer: List<Footer>,
            val otherinfo: List<Otherinfo>,
            val courses: Courses,
            val shipping: List<Shipping>,
            @SerializedName("shipping_name")
            val shippingName: String
    ) {
        data class Courses(
                val coursesData: List<CoursesData>,
                val title: String
        ) {
            data class CoursesData(
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
                    @SerializedName("modified_date")
                    val modifiedDate: String,
                    @SerializedName("order_id")
                    val orderId: Int,
                    @SerializedName("order_note")
                    val orderNote: String,
                    @SerializedName("orderproduct_id")
                    val orderproductId: Int,
                    val params: String,
                    @SerializedName("parent_order_id")
                    val parentOrderId: Int,
                    val price: String,
                    @SerializedName("product_id")
                    val productId: Int,
                    val quantity: Int,
                    @SerializedName("shipping_delivery_tile")
                    val shippingDeliveryTile: String,
                    @SerializedName("shipping_taxes")
                    val shippingTaxes: String,
                    val sku: String,
                    val state: String,
                    @SerializedName("store_id")
                    val storeId: Int,
                    val subtotal: String,
                    val images: ProductImages,
                    val title: String,
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
                data class ProductImages(
                        val main: Main
                ){
                    data class Main(
                            val icon: String,
                            val main: String,
                            val normal: String,
                            val normalMain: String
                    )
                }
            }
        }

        data class Billing(
                val address: String,
                @SerializedName("billing_name")
                val billingName: String,
                val city: String,
                val email: String,
                val name: String,
                @SerializedName("phone_number")
                val phoneNumber: String,
                val phonecode: Int,
                @SerializedName("state_name")
                val stateName: String
        )

        data class Footer(
                val label: String,
                val name: String
        )

        data class Shipping(
                val address: String,
                val city: String,
                val email: String,
                val name: String,
                @SerializedName("phone_number")
                val phoneNumber: String,
                val phonecode: Int,
                @SerializedName("shipping_name")
                val shippingName: String,
                @SerializedName("state_name")
                val stateName: String
        )

        data class Otherinfo(
                val label: String,
                val name: String
        )
    }
}