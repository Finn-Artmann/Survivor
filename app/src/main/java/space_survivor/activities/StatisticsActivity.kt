package space_survivor.activities

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.soywiz.klock.ISO8601
import com.soywiz.klock.TimeSpan
import space_survivor.R
import space_survivor.databinding.ActivityStatisticsBinding
import space_survivor.main.MainApp
import java.text.ParseException
import java.text.SimpleDateFormat
import timber.log.Timber.i
import java.util.*
import kotlin.time.Duration.Companion.minutes

class StatisticsActivity : AppCompatActivity() {

    lateinit var app : MainApp
    private lateinit var binding: ActivityStatisticsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)

        binding.buttonZoomOut.setOnClickListener{
            drawPlayerStats()
        }

        drawPlayerStats()
    }

    override fun onResume() {
        super.onResume()
        drawPlayerStats()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val imageView = findViewById<ImageView>(R.id.imageView2)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            imageView.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    private fun drawPlayerStats(){

        val graphView: GraphView = binding.root.findViewById(R.id.graph_view)
        val series = LineGraphSeries<DataPoint>()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())

        // Format graph view
        graphView.gridLabelRenderer.setHorizontalLabelsAngle(135)
        graphView.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
        graphView.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    return formatter.format(value)
                } else {
                    val minutes = (value / 1000 / 60).toInt()
                    val seconds = (value / 1000 % 60).toInt()
                    return "$minutes:$seconds"
                }
            }
        }
        graphView.gridLabelRenderer.numHorizontalLabels = 5
        graphView.gridLabelRenderer.textSize = 20f
        graphView.gridLabelRenderer.padding = 50
        graphView.gridLabelRenderer.verticalAxisTitle = "Score"
        graphView.gridLabelRenderer.horizontalAxisTitle = "Date"
        graphView.gridLabelRenderer.padding
        graphView.gridLabelRenderer.isHighlightZeroLines = true
        graphView.gridLabelRenderer.isHorizontalLabelsVisible = true
        graphView.gridLabelRenderer.isVerticalLabelsVisible = true


        graphView.legendRenderer.isVisible = true
        graphView.legendRenderer.textSize = 20f
        graphView.legendRenderer.padding = 50
        graphView.legendRenderer.align = LegendRenderer.LegendAlign.TOP


        series.title = "Player Score"
        series.color = Color.MAGENTA
        series.isDrawBackground = true
        series.backgroundColor = Color.argb(50, 255, 0, 255)
        series.isDrawDataPoints = true
        series.setOnDataPointTapListener{ _, dataPoint ->

            var time = TimeSpan(dataPoint.y);

            val text = "You survived for " +  time.hours.toInt() % 60 + " hours, " + time.minutes.toInt() % 60 +
                            " minutes and " + time.seconds.toInt() % 60 + " seconds on " +
                            formatter.format(dataPoint.x) + "."

            val snackbar = Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.BLACK)
            snackbar.show()

            // Make sure the background color/alpha does not change
            series.backgroundColor = Color.argb(50, 255, 0, 255)
        }

        // Get all scores of currently logged in user
        if(app.account != null){
            i("Account: ${app.account}")
            val scores = app.scores.findAll().filter { it.playerName == app.account!!.displayName}

            // Sort scores by date and Time
            val sortedScores = scores.sortedBy { it.dateAndTime}
            var maxScore = 0L
            var minScore: Long? = null
            var maxDate : java.util.Date? = null
            var minDate : java.util.Date? = null


            // Add scores to graph
            for (score in sortedScores){
                try {
                    val date = score.dateAndTime?.let { formatter.parse(it) }
                    if (date != null && score.score != null) {

                        if(score.score!! > maxScore){
                            maxScore = score.score!!
                        }

                        if(minScore == null) {
                            minScore = score.score!!
                        } else if(score.score!! < minScore){
                            minScore = score.score!!
                        }

                        if(maxDate == null){
                            maxDate = date
                        }

                        if(maxDate < date){
                            maxDate = date
                        }

                        if(minDate == null){
                            minDate = date

                        }

                        if(minDate > date){
                            minDate = date
                        }

                        series.appendData(DataPoint(date, score.score!!.toDouble()), true, 100)
                    }
                } catch (e: ParseException) {
                    // handle the exception
                    i("ERROR: $e")
                }
            }

            if(maxDate != null && minDate != null){
                graphView.viewport.setMinX(minDate!!.time.toDouble())
                graphView.viewport.setMaxX(maxDate!!.time.toDouble())
                graphView.viewport.setMaxY(maxScore.toDouble())
                if (minScore != null) {
                    graphView.viewport.setMinY(minScore.toDouble())
                }
                graphView.viewport.isXAxisBoundsManual = true
                graphView.viewport.isYAxisBoundsManual = true
            }

        }
        else{
            i("Account is null")
            // Show toast message if user is not logged in
            Toast.makeText(this, "Please log in to view your statistics", Toast.LENGTH_LONG).show()
        }

        graphView.removeAllSeries()
        graphView.addSeries(series)
        graphView.animate()
        graphView.viewport.isScalable = true
        graphView.viewport.isScrollable = true
        graphView.viewport.setScalableY(true)
        graphView.viewport.setScrollableY(true)
    }

}
