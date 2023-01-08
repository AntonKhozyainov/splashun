package ru.khozyainov.splashun.utils

import androidx.recyclerview.widget.DiffUtil
import ru.khozyainov.splashun.models.DataForDiffUtil

//Реализация DiffUtil для использования с data классами, реализующими интерфейс DataForDiffUtil
class  DiffUtilForDataClass<T: DataForDiffUtil> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}