


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eco.customcalendar.data.Note
import com.eco.customcalendar.utils.Converters

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                // TODO("Tại sao phải check INSTANCE ?:")
                /*
                Lượt check 1: INSTANCE ?:
                    Check nhanh ở ngoài synchronized để tránh lock không cần thiết nếu database đã được tạo rồi.
                    Nếu đã có instance → trả về luôn → nhanh hơn.
                Lượt check 2 (trong synchronized): INSTANCE ?:
                    Khi INSTANCE là null, các thread sẽ cùng nhau vào synchronized block.
                    Trong synchronized, check lại 1 lần nữa để chắc chắn chưa có thread nào tạo database trước đó.
                    Vì có thể Thread A đang tạo database, Thread B cũng vào synchronized trong lúc đó
                    → nếu không check lại, có thể tạo 2 instance → lỗi nghiêm trọng trong singleton pattern.
                */
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calendar_notes.db"
                ).build().also { INSTANCE = it }
            }
    }
}
