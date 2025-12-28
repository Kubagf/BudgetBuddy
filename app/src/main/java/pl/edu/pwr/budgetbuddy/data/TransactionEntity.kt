package pl.edu.pwr.budgetbuddy.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "transactions", foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.RESTRICT
    )], indices = [Index("categoryId")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val categoryId: Int,
    val description: String?,
    val date: Date,
    val receiptImagePath: String? = null,
    val type: TransactionType = TransactionType.EXPENSE
)

enum class TransactionType { EXPENSE, INCOME }