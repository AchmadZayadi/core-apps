package com.sesolutions.responses.Courses.course

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.PaginationHelper
import com.sesolutions.responses.store.checkout.Order

data class CheckoutResponse2(val result: Result,
                            @SerializedName("session_id")
                            val sessionId: String) : ErrorResponse() {
    data class Result(
            val cartData: List<CartData>?,
            val extraParams: ExtraParams,
            @SerializedName("price_title")
            val priceTitle: String,
            val priceDetails: List<PriceDetails>,
            val checkout: String,
            val orders: List<Order>?,
            val grand_total: String,
            val checkouturl: String,
            @SerializedName("order_total")
            val orderTotal: String
    ) : PaginationHelper() {
        data class ExtraParams(
                @SerializedName("continue")
                val continueX: String,
                @SerializedName("cart_total")
                val cart_count: Int,
                val empty: String,
                val update: String
        )

        data class CartData(
                val productData: List<ProductData>,
                @SerializedName("title")
                val storeTitle: String,
                @SerializedName("price")
                val subTotal: String
        ) {
            data class ProductData(
                    val buttons: List<Button>,

                    val price: String,
                    @SerializedName("course_id")
                    val productId: Int,
                    val quantity: Int,
                    @SerializedName("cart_error")
                    val cartError: String,
                    @SerializedName("course_images")
                    val courseImages: CourseImages,
                    @SerializedName("taxes")
                    val taxes: Taxes,
                    val title: String
            ) {
                data class Button(
                        val id: Int,
                        val label: String,
                        val name: String
                )
                data class Taxes(
                        val total_tax: Int
                )

                data class CourseImages(
                        val icon: String,
                        val main: String,
                        val normal: String,
                        val normalMain: String
                )
            }
        }

        data class PriceDetails(
                @SerializedName("title")
                val title: String,
                @SerializedName("price")
                val price: String
        )
    }
}