package ru.teamdroid.colibripost.ui.main.calendar

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.ItemCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.WeekHolder>() {
     val weeks =
            Week.getWeekAround(6) //нужно ли делать бесконечный скролл или мы будем отображать посты в определённом периоде?

    lateinit var currentWeek:Week
    var currentPosition:Int = 6
    var isFragmentLaunch:Boolean = true

    var selectedDay =
            Day(System.currentTimeMillis())
        private set(value) {
            previousSelectedDay = field
            field = value
            refreshSelectedDays()
            calendarClickListener?.onDaySelected(value)
        }
    private var previousSelectedDay = selectedDay
    var calendarClickListener: CalendarClickListener? = null

    var lastPositionCounter: Int = 0

    lateinit var loadPostsByData:()->Unit
    lateinit var cacheIndicateDaysOfWeek:(days:List<Int>, months:List<Int>, years:List<Int>, setUpDays:(week:Week, postExisting:List<Boolean>)->Unit)->Unit
    lateinit var remoteIndicateDaysOfWeek:(times:List<Long>, setUpDays:(week:Week, postExisting:List<Boolean>)->Unit)->Unit
    lateinit var indicateEndOfList:()->Unit
    lateinit var indicateStartOfList:()->Unit
    lateinit var indicateMiddleOfList:()->Unit


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCalendarBinding.inflate(inflater, parent, false)
        return WeekHolder(binding, remoteIndicateDaysOfWeek)
    }

    override fun onBindViewHolder(holder: WeekHolder, position: Int) {
        currentWeek = weeks[position]
        holder.remoteIndicateDaysOfWeek = remoteIndicateDaysOfWeek
        holder.bind(weeks[position], position)
    }

    override fun getItemCount(): Int {
        return weeks.size
    }


    fun getWeek(position: Int): Week {
        return weeks[position]
    }

    fun getPageOfDay(day: Day): Int {
        weeks.forEachIndexed { index, week ->
            if (week.containsDay(day)) {
                return index
            }
        }
        throw java.lang.IllegalStateException("day not in adapter")
    }

    fun selectDay(day: Day) {
        selectedDay = day
        refreshSelectedDays()
        notifyItemChanged(getPageOfDay(day))
    }

    //предварительный метод установки постов
    suspend fun setData(posts: List<Post>) = withContext(Dispatchers.Default) {
        val list = mutableListOf<Day>()
        posts.forEach { post ->
            val day =
                    Day(post.time)
            day.delayedPost = if (post.success) {
                Day.DelayedPosts.DELAYED
            } else {
                Day.DelayedPosts.ERROR
            }
            list.add(day)
        }
        for (week in weeks) {
            for (day in list) {
                if (week.containsDay(day)) {
                    val pos = week.getPositionOfDay(day)
                    week.weekDays[pos] = day
                }
            }
        }
        withContext(Dispatchers.Main) {
            notifyDataSetChanged()
        }

    }

    private fun refreshSelectedDays() {
        var previousSelectedWeek = 0
        var selectedWeek = 0
        weeks.forEachIndexed { index, week ->
            if (week.containsDay(previousSelectedDay)) {
                previousSelectedWeek = index
            }
            if (week.containsDay(selectedDay)) {
                selectedWeek = index
            }
        }
        if (selectedWeek != previousSelectedWeek) {
            notifyItemChanged(previousSelectedWeek)
        }
    }

    fun showPositions(parent: ViewGroup, position: Int){
        onBindViewHolder(WeekHolder(binding=ItemCalendarBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)), position)
    }

    inner class WeekHolder(private val binding: ItemCalendarBinding,
                           var remoteIndicateDaysOfWeek:(times:List<Long>, setUpDays:(week:Week, postExisting:List<Boolean>)->Unit)->Unit = {_, _ ->},
                           var indicateEndOfList:()->Unit={},
                           var indicateStartOfList:()->Unit={},
                           var indicateMiddleOfList:()->Unit={}) :
            RecyclerView.ViewHolder(binding.root) {

        private val calendar = Calendar.getInstance()
        private val set = ConstraintSet()
        var animatorHelper: AnimatorHelper? = null

        fun bind(week: Week, position: Int) = with(binding) {

            //region UI Init
            tvNumberFirst.text = week.getNumberOfMonth(1).toString()
            tvNumberSecond.text = week.getNumberOfMonth(2).toString()
            tvNumberThird.text = week.getNumberOfMonth(3).toString()
            tvNumberFourth.text = week.getNumberOfMonth(4).toString()
            tvNumberFifth.text = week.getNumberOfMonth(5).toString()
            tvNumberSixth.text = week.getNumberOfMonth(6).toString()
            tvNumberSeventh.text = week.getNumberOfMonth(7).toString()

            tvFirstDayWeek.text = getWeekDay(1)
            tvSecondDayWeek.text = getWeekDay(2)
            tvThirdDayWeek.text = getWeekDay(3)
            tvForthDayWeek.text = getWeekDay(4)
            tvFifthDayWeek.text = getWeekDay(5)
            tvSixthDayWeek.text = getWeekDay(6)
            tvSeventhDayWeek.text = getWeekDay(7)

            tvNumberFirst.setOnClickListener { onClick(week, 1) }
            tvNumberSecond.setOnClickListener { onClick(week, 2) }
            tvNumberThird.setOnClickListener { onClick(week, 3) }
            tvNumberFourth.setOnClickListener { onClick(week, 4) }
            tvNumberFifth.setOnClickListener { onClick(week, 5) }
            tvNumberSixth.setOnClickListener { onClick(week, 6) }
            tvNumberSeventh.setOnClickListener { onClick(week, 7) }

            tvFirstDayWeek.setOnClickListener { onClick(week, 1) }
            tvSecondDayWeek.setOnClickListener { onClick(week, 2) }
            tvThirdDayWeek.setOnClickListener { onClick(week, 3) }
            tvForthDayWeek.setOnClickListener { onClick(week, 4) }
            tvFifthDayWeek.setOnClickListener { onClick(week, 5) }
            tvSixthDayWeek.setOnClickListener { onClick(week, 6) }
            tvSeventhDayWeek.setOnClickListener { onClick(week, 7) }

            tvNumberFirst.setTextColor(
                    ContextCompat.getColor(
                            binding.root.context,
                            R.color.text
                    )
            )
            tvNumberSecond.setTextColor(
                    ContextCompat.getColor(
                            binding.root.context,
                            R.color.text
                    )
            )
            tvNumberThird.setTextColor(
                    ContextCompat.getColor(
                            binding.root.context,
                            R.color.text
                    )
            )
            tvNumberFourth.setTextColor(
                    ContextCompat.getColor(
                            binding.root.context,
                            R.color.text
                    )
            )
            tvNumberFifth.setTextColor(
                    ContextCompat.getColor(
                            binding.root.context,
                            R.color.text
                    )
            )
            tvNumberSixth.setTextColor(
                    ContextCompat.getColor(
                            binding.root.context,
                            R.color.text
                    )
            )
            tvNumberSeventh.setTextColor(
                    ContextCompat.getColor(
                            binding.root.context,
                            R.color.text
                    )
            )

            //endregion

            //indicateDaysOfWeek(getCacheDays(week), getCacheMonth(week), getCacheYears(week), ::setUpDaysIndicator)

            Log.d("ViewPagerCheck", "CalendarAdapter" + position.toString())
            /*if(currentPosition == position)*/ remoteIndicateDaysOfWeek(getWeekTimes(week), ::setUpDaysIndicator)


            setupSelection(week)
            val oneWeek = week
            animatorHelper =
                    AnimatorHelper(
                            this,
                            week
                    )
        }

        //region Date Data

        private fun getCacheDays(week: Week):List<Int>{
            return listOf(week.dayOfWeek(1).dayOfMonth, week.dayOfWeek(2).dayOfMonth, week.dayOfWeek(3).dayOfMonth, week.dayOfWeek(4).dayOfMonth,
                    week.dayOfWeek(5).dayOfMonth, week.dayOfWeek(6).dayOfMonth, week.dayOfWeek(7).dayOfMonth)
        }

        private fun getCacheMonth(week: Week):List<Int>{
            return listOf(week.dayOfWeek(1).month, week.dayOfWeek(2).month, week.dayOfWeek(3).month, week.dayOfWeek(4).month,
                    week.dayOfWeek(5).month, week.dayOfWeek(6).month, week.dayOfWeek(7).month)
        }

        private fun getCacheYears(week: Week):List<Int>{
            return listOf(week.dayOfWeek(1).year, week.dayOfWeek(2).year, week.dayOfWeek(3).year, week.dayOfWeek(4).year,
                    week.dayOfWeek(5).year, week.dayOfWeek(6).year, week.dayOfWeek(7).year)
        }

        private fun getWeekTimes(week: Week):List<Long>{
            return listOf(week.dayOfWeek(1).time, week.dayOfWeek(2).time, week.dayOfWeek(3).time, week.dayOfWeek(4).time,
                    week.dayOfWeek(5).time, week.dayOfWeek(6).time, week.dayOfWeek(7).time)
        }

        //endregion

        //region UI Control

        private fun setUpDaysIndicator(week:Week, existingPostsOnWeek:List<Boolean>){
            var i = 1
            Log.d("ViewPagerCheck", "SetUpDays " + adapterPosition)
            for(isExist:Boolean in existingPostsOnWeek){
                week.dayOfWeek(i).delayedPost = if(isExist)  Day.DelayedPosts.DELAYED
                else Day.DelayedPosts.NONE
                i++
            }

            setupStateUnderView(binding.viewUnderDay1, week.dayOfWeek(1))
            setupStateUnderView(binding.viewUnderDay2, week.dayOfWeek(2))
            setupStateUnderView(binding.viewUnderDay3, week.dayOfWeek(3))
            setupStateUnderView(binding.viewUnderDay4, week.dayOfWeek(4))
            setupStateUnderView(binding.viewUnderDay5, week.dayOfWeek(5))
            setupStateUnderView(binding.viewUnderDay6, week.dayOfWeek(6))
            setupStateUnderView(binding.viewUnderDay7, week.dayOfWeek(7))
        }

        private fun setupStateUnderView(view: View, day: Day) {
            when (day.delayedPost) {
                Day.DelayedPosts.NONE -> view.backgroundTintList =
                        ColorStateList.valueOf(Color.TRANSPARENT)
                Day.DelayedPosts.DELAYED -> view.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(view.context, R.color.accent))
                Day.DelayedPosts.ERROR -> view.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(view.context, R.color.red))
            }
        }

        private fun setupSelection(week: Week) {
            if (week.containsDay(selectedDay)) {
                val positionOfDay = week.getPositionOfDay(selectedDay)
                changeConstraintSelection(positionOfDay)
            } else {
                removeDaySelection()
            }
        }

        private fun removeDaySelection() {
            set.clone(binding.container)
            set.clear(binding.viewSelectedDay.id, ConstraintSet.START)
            set.clear(binding.viewSelectedDay.id, ConstraintSet.END)
            set.clear(binding.viewSelectedDay.id, ConstraintSet.LEFT)
            set.clear(binding.viewSelectedDay.id, ConstraintSet.RIGHT)
            set.connect(binding.viewSelectedDay.id,
                    ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
            )
            set.applyTo(binding.container)
        }

        private fun idSelectedDay(day: Int): Int {
            return when (day) {
                1 -> binding.tvNumberFirst.id
                2 -> binding.tvNumberSecond.id
                3 -> binding.tvNumberThird.id
                4 -> binding.tvNumberFourth.id
                5 -> binding.tvNumberFifth.id
                6 -> binding.tvNumberSixth.id
                7 -> binding.tvNumberSeventh.id
                else -> throw IllegalStateException("idSelectedDay")
            }
        }


        private fun onClick(week: Week, dayWeek: Int) {
            selectedDay = week.dayOfWeek(dayWeek)
            animatorHelper?.animateChangeSelection(selectedDay, previousSelectedDay)
            loadPostsByData()
        }


        private fun changeConstraintSelection(position: Int) {
            set.clone(binding.container)
            setupViewPosition(idSelectedDay(position))
            set.applyTo(binding.container)
        }

        private fun setupViewPosition(viewId: Int) {
            set.clear(binding.viewSelectedDay.id, ConstraintSet.START)
            set.clear(binding.viewSelectedDay.id, ConstraintSet.END)
            set.clear(binding.viewSelectedDay.id, ConstraintSet.LEFT)
            set.clear(binding.viewSelectedDay.id, ConstraintSet.RIGHT)
            set.connect(binding.viewSelectedDay.id,
                    ConstraintSet.START, viewId,
                    ConstraintSet.START
            )
            set.connect(binding.viewSelectedDay.id, ConstraintSet.END, viewId, ConstraintSet.END)
        }

        private fun getWeekDay(day: Int): String {
            calendar.set(Calendar.DAY_OF_WEEK, day)
            return SimpleDateFormat("E", Locale.getDefault())
                    .format(calendar.time).toLowerCase(Locale.getDefault())
        }

        fun setImageButtonState(position: Int){
            when(position){
                weeks.size - 1 -> {

                    indicateEndOfList()
                }
                0 -> {
                    indicateStartOfList()
                }
                else -> {
                    indicateMiddleOfList()
                }
            }
        }

        //endregion

    }

}