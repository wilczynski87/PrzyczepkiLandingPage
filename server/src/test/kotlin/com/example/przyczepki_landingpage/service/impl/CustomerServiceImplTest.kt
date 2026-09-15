package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Private
import com.example.przyczepki_landingpage.support.FakeCustomerRepo
import io.ktor.server.plugins.BadRequestException
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CustomerServiceImplTest {

    @Test
    fun `update throws when customer id is missing`() = runBlocking {
        val service = CustomerServiceImpl("http://localhost/", FakeCustomerRepo())

        val error = assertFailsWith<BadRequestException> {
            service.update(Customer(private = Private(email = "a@b.pl")))
        }

        assertEquals("Brak id klienta", error.message)
    }

    @Test
    fun `update returns null when customer does not exist`() = runBlocking {
        val service = CustomerServiceImpl("http://localhost/", FakeCustomerRepo())

        val result = service.update(
            Customer(id = "missing", private = Private(email = "a@b.pl")),
        )

        assertNull(result)
    }

    @Test
    fun `update keeps confirmed and changes profile fields`() = runBlocking {
        val repo = FakeCustomerRepo()
        repo.addCustomer(
            Customer(
                id = "c1",
                confirmed = "2026-01-01",
                private = Private(firstName = "Jan", lastName = "Kowalski", email = "jan@example.com"),
            ),
        )
        val service = CustomerServiceImpl("http://localhost/", repo)

        val result = service.update(
            Customer(
                id = "c1",
                confirmed = "spoofed",
                private = Private(firstName = "Anna", lastName = "Nowak", email = "jan@example.com"),
            ),
        )

        assertEquals("Anna", result?.private?.firstName)
        assertEquals("Nowak", result?.private?.lastName)
        assertEquals("2026-01-01", result?.confirmed)
    }

    @Test
    fun `update throws when email belongs to another customer`() = runBlocking {
        val repo = FakeCustomerRepo()
        repo.addCustomer(Customer(id = "c1", private = Private(email = "one@example.com")))
        repo.addCustomer(Customer(id = "c2", private = Private(email = "two@example.com")))
        val service = CustomerServiceImpl("http://localhost/", repo)

        val error = assertFailsWith<BadRequestException> {
            service.update(Customer(id = "c1", private = Private(email = "TWO@example.com")))
        }

        assertEquals("Klient z tym adresem e-mail już istnieje", error.message)
    }

    @Test
    fun `update allows keeping the same email with different case`() = runBlocking {
        val repo = FakeCustomerRepo()
        repo.addCustomer(
            Customer(id = "c1", private = Private(firstName = "Jan", email = "jan@example.com")),
        )
        val service = CustomerServiceImpl("http://localhost/", repo)

        val result = service.update(
            Customer(id = "c1", private = Private(firstName = "Janek", email = "JAN@example.com")),
        )

        assertEquals("Janek", result?.private?.firstName)
        assertEquals("JAN@example.com", result?.private?.email)
    }

    @Test
    fun `delete returns true when customer exists`() = runBlocking {
        val repo = FakeCustomerRepo()
        repo.addCustomer(Customer(id = "c1"))
        val service = CustomerServiceImpl("http://localhost/", repo)

        assertTrue(service.delete("c1"))
        assertNull(repo.get("c1"))
    }

    @Test
    fun `delete returns false when customer is missing`() = runBlocking {
        val service = CustomerServiceImpl("http://localhost/", FakeCustomerRepo())

        assertFalse(service.delete("missing"))
        assertFalse(service.delete(""))
    }
}
