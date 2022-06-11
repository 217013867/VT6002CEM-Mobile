import com.example.fishing.Register
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterTest {

    @Mock
    var register: Register? = Mockito.mock(Register::class.java)

    @Test
    fun validate() {
        print(register)
        print(register?.validate("name@email.com", "email"))
        val sss = register!!.validate("name@email.com", "email")
        assertFalse(register!!.validate("name@email.com", "email"))
    }
}