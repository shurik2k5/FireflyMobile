package xyz.hisname.fireflyiii.data.local.dao

import androidx.room.*
import xyz.hisname.fireflyiii.repository.models.bills.BillData

@Dao
abstract class BillDataDao: BaseDao<BillData>{

    @Query("SELECT * FROM bills")
    abstract fun getAllBill(): MutableList<BillData>

    @Query("DELETE FROM bills WHERE billId = :billId")
    abstract fun deleteBillById(billId: Long): Int

    @Query("SELECT * FROM bills WHERE billId = :billId")
    abstract fun getBillById(billId: Long): MutableList<BillData>

    @Query("DELETE FROM bills")
    abstract fun deleteAllBills(): Int
}
