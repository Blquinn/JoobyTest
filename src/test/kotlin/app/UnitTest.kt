package app

import me.blq.standupapi.commons.Page
import me.blq.StandupBot.standups.*
import me.blq.standupapi.standups.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import javax.persistence.*

class MockStandupRepository : StandupRepository {
    override fun listStandups(filters: StandupFilters): Page<Standup> {
        return Page(null, listOf(Standup(1, "")))
    }

    override fun getStandupByName(name: String): Standup? {
        return null
    }

    override fun getStandupById(id: Long): Standup? {
        return null
    }

    override fun save(s: Standup): Standup {
        s.id = 1
        return s
    }
}

data class JPAMocks(
    val emf: EntityManagerFactory,
    val em: EntityManager,
    val tx: EntityTransaction,
)

fun getJPAMocks(): JPAMocks {
    val mockTx = mock(EntityTransaction::class.java)
    `when`(mockTx.isActive).thenReturn(false)

    val mockEm = mock(EntityManager::class.java)
    `when`(mockEm.isOpen).thenReturn(false)
    `when`(mockEm.transaction).thenReturn(mockTx)

    val mockEmf = mock(EntityManagerFactory::class.java)
    `when`(mockEmf.createEntityManager()).thenReturn(mockEm)

    return JPAMocks(mockEmf, mockEm, mockTx)
}

class UnitTest {
    @Test
    fun welcome() {
        val mocks = getJPAMocks()

        val repo = MockStandupRepository()
        val service = StandupService(mocks.emf, repo)
        val s = StandupCreateDto("bar")
        val ss = service.createStandup(s)

        Assertions.assertEquals(1, ss.id)
        Assertions.assertEquals(s.name, ss.name)
    }
}
