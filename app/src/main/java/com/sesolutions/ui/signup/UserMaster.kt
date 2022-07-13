package com.sesolutions.ui.signup

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.ValidateFieldError
import com.sesolutions.responses.feed.Images
import com.sesolutions.responses.feed.Options
import com.sesolutions.utils.Constant

/**
 * Created by root on 2/11/17.
 */

open class UserMaster {

    @SerializedName("user_id")
    var userId: Int = 0
    @SerializedName("resource_id")
    val resourceId: Int = 0
    @SerializedName("email")
    var email: String? = null
    val name: String? = null
    @SerializedName("username")
    var username: String? = null
    @SerializedName("displayname")
    var displayname: String? = null
        get() = if (field != null) field else memberTitle
    @SerializedName("member_title")
    val memberTitle: String? = null
    @SerializedName("member_photo")
    val memberPhoto: String? = null
    private val RSVP: String? = null
    private val rsvp: Int = 0
    @SerializedName("photo_id")
    var photoId: Int = 0
    @SerializedName("status")
    var status: String? = null
    @SerializedName("status_date")
    var statusDate: String? = null
    @SerializedName("password")
    var password: String? = null

    @SerializedName("default_currency")
    var defaultcurrency: String? = null

    @SerializedName("salt")
    var salt: String? = null
    @SerializedName("locale")
    var locale: String? = null
    @SerializedName("language")
    var language: String? = null
    @SerializedName("timezone")
    var timezone: String? = null
    @SerializedName("search")
    var search: Int = 0
    @SerializedName("show_profileviewers")
    var showProfileviewers: Int = 0
    @SerializedName("level_id")
    var levelId: Int = 0
    @SerializedName("invites_used")
    var invitesUsed: Int = 0
    @SerializedName("extra_invites")
    var extraInvites: Int = 0
    @SerializedName("enabled")
    var enabled: Int = 0
    @SerializedName("verified")
    var verified: Int = 0
    @SerializedName("approved")
    var approved: Int = 0
    @SerializedName("creation_date")
    var creationDate: String? = null
    @SerializedName("modified_date")
    var modifiedDate: String? = null
    @SerializedName("lastlogin_date")
    var lastloginDate: String? = null
    @SerializedName("member_count")
    var memberCount: Int = 0
    @SerializedName("view_count")
    var viewCount: Int = 0
    @SerializedName("comment_count")
    var commentCount: Int = 0
    @SerializedName("like_count")
    var likeCount: Int = 0
    @SerializedName("blocked_levels")
    var blockedLevels: String? = null
    @SerializedName("blocked_networks")
    var blockedNetworks: String? = null
    @SerializedName("infomusic_playlist")
    var infomusicPlaylist: Int = 0
    @SerializedName("cover")
    var cover: Int = 0
    @SerializedName("cover_position")
    var coverPosition: String? = null
    @SerializedName("follow_count")
    var followCount: Int = 0
    @SerializedName("ic_location")
    var location: String? = null
    @SerializedName("rating")
    var rating: Double = 0.toDouble()
    @SerializedName("user_verified")
    var userVerified: Int = 0
    @SerializedName("cool_count")
    var coolCount: Int = 0
    @SerializedName("funny_count")
    var funnyCount: Int = 0
    @SerializedName("useful_count")
    var usefulCount: Int = 0
    @SerializedName("featured")
    var featured: Int = 0
    @SerializedName("sponsored")
    var sponsored: Int = 0
    @SerializedName("vip")
    var vip: Int = 0
    @SerializedName("offtheday")
    var offtheday: Int = 0
    @SerializedName("photo_url")
    var photoUrl: String? = null
    @SerializedName("loggedin_user_id")
    var loggedinUserId: Int = 0
    @SerializedName("owner_photo")
    private val ownerPhoto: JsonElement? = null
    private val images: Images? = null

    val profileImageUrl: String?
        get() = images?.getProfile()

    //used in event member and page member
    val options: List<Options>? = null

    //mannualy adding token field
    var authToken: String? = null

    var valdateFieldsError: List<ValidateFieldError>? = null

    /* public <T> T getOwnerPhoto() {
        if (null != ownerPhoto) {
            if (ownerPhoto.isJsonObject()) {
                return (T) new Gson().fromJson(ownerPhoto, Images.class);
            } else {
                return (T) ownerPhoto.getAsString();
            }
        }
        return null;
    }*/

    fun getRSVP(): String? {
        return RSVP
    }

    fun getRsvp(): Int {
        return rsvp
    }

    fun getOwnerPhoto(): String? {
        return if (null != ownerPhoto) {
            if (ownerPhoto.isJsonObject) {
                Gson().fromJson(ownerPhoto, Images::class.java).getNormal()
            } else {
                ownerPhoto.asString
            }
        } else null
    }

    fun fetchFirstNErrors(): String {
        var errors = Constant.EMPTY
        if (null != valdateFieldsError && valdateFieldsError!!.size > 0) {
            val totalCount = if (valdateFieldsError!!.size > Constant.SHOW_TOTAL_ERROR_COUNT)
                Constant.SHOW_TOTAL_ERROR_COUNT
            else
                valdateFieldsError!!.size
            for (i in 0 until totalCount) {
                errors = errors + valdateFieldsError!![i].errormessage + "\n"
            }
        }
        return errors.trim { it <= ' ' }
    }

}
