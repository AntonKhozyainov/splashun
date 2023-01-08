package ru.khozyainov.splashun.utils

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

//ItemOffsetDecoration класс для создания отступов между элементами списка
class ItemOffsetDecoration(
    private val context: Context
): RecyclerView.ItemDecoration() {

    //Переопределяем метод для указания отступов между элементами списка
    //outRect определяет количество пикселей отступа с каждой стороны
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        //super.getItemOffsets(outRect, view, parent, state)

        val offset = 4.fromDpToPixels(context)
        with(outRect){
            top = offset
            right = offset
            left = offset
            bottom = offset
        }

    }
}

//Переводим dp в пиксели
fun Int.fromDpToPixels(context: Context) : Int{
    //Получаем плотность пикселей экрана устройства и делим на стандартную плотность (160 Dpi)
    //и умножаем текущее число на количество пикселей в одном dp
    return context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT * this
}

//Переводим пиксели в dp
fun Int.fromPixelsToDp(context: Context) : Int=
    context.resources.displayMetrics.densityDpi * DisplayMetrics.DENSITY_DEFAULT / this