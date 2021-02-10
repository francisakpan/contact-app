package com.francis.week6

import com.francis.week6.models.Contact
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidatorTest {

    @Test
    fun validateContact() {
        var contact =
            Contact(fullName = "", phone = "08333244434", email = "francis@decagon.dev", color = 0)
        var newContact = Validator.setContactName(contact)
        assertEquals(
            "Empty contact name should take name of email or phone field if available",
            contact.email,
            newContact.fullName)

        contact = Contact(fullName = "", phone = "08333244434", email = "", color = 0)
        newContact = Validator.setContactName(contact)
        assertEquals(
            "Empty contact name should take phone field since email field is empty",
            contact.phone,
            newContact.fullName
        )
    }
}