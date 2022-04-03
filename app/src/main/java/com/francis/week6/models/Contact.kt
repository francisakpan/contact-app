package com.francis.week6.models

import android.graphics.Color
import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

/**
 * Models Each contact object.
 */
@Parcelize
data class Contact(
    @get: Exclude //Exclude database from saving id field
    var id: String? = null,

    @get: Exclude //Exclude database from saving isDeleted field
    var isDeleted: Boolean? = false,

    var fullName: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var color: Int = Color.argb(255, 156, 39, 176)
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return if (other is Contact){
            other.id == id
        }else false
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (fullName?.hashCode() ?: 0)
        return result
    }
}