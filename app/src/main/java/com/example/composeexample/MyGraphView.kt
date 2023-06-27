package com.example.composeexample

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MyGraphView(context: Context?) : View(context) {
    private lateinit var path: Path
    private var mPaint: Paint? = null //объект для параметров рисования графических примитивов
    private var mBitmapPaint: Paint? = null //объект для параметров вывода битмапа на холст
    private var mBitmap : Bitmap? = null //сам битмап
    private var mCanvas: Canvas? = null //холст
    init { //секция инициализации полей класса
//создаем объект класса Paint для параметров вывода битмапа на холст
        mBitmapPaint = Paint(Paint.DITHER_FLAG) // Paint.DITHER_FLAG – для эффекта сглаживания
        mPaint = Paint()//создаем объект класса Paint для параметров рисования графики
        mPaint!!.setAntiAlias(true) //устанавливаем антиалиасинг (сглаживание)
        mPaint?.setColor(Color.GREEN) //цвет рисования
        mPaint?.setStyle(Paint.Style.STROKE) //стиль рисования
//(Paint.Style.STROKE – без заполнения)
        mPaint?.setStrokeJoin(Paint.Join.ROUND) //стиль соединения линий (ROUND - скруглённый)
        mPaint?.setStrokeCap(Paint.Cap.ROUND) //стиль концов линий (ROUND - скруглённый)
        mPaint?.setStrokeWidth(12F) //толщина линии рисования
    }
    //метод onSizeChanged вызывается первый раз при создании объекта,
//далее – при изменении размера объекта, нам он нужен для выяснения первичных размеров битмапа
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//создаем битмап с высотой и шириной как у текущего объекта и с параметром Bitmap.Config.ARGB_8888,
//это четырехканальный RGB (прозрачность и 3 цвета)
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!) //создаем канвас и связываем его с битмапом
        Toast.makeText(this.context, "onSizeChanged ", Toast.LENGTH_SHORT).show() //для отладки
    }
    //метод перерисовки объекта, он будет срабатывать каждый раз
    override fun onDraw(canvas: Canvas) { //при вызове функции invalidate() текущего объекта
        super.onDraw(canvas)
//отрисовываем на канвасе текущего объекта (не путать с созданным нами канвасом) наш битмап
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint!!)
    }
    fun drawCircle() { //метод для рисования круга
        println("mCanvas = $mCanvas")
        mCanvas!!.drawCircle(100f, 100f, 50f, mPaint!!)
        invalidate() //для срабатывания метода onDraw
    }
    fun drawSquare() { //метод для рисования квадрата
        println("mCanvas = $mCanvas")
        mCanvas!!.drawRect(200f, 200f, 300f, 300f, mPaint!!)
        invalidate()
    }
    fun drawFace() { //метод для рисования картинки из файла
//создаем временный битмап из файла
        val mBitmapFromSdcard = BitmapFactory.decodeFile("/mnt/sdcard/face.png")
        mCanvas!!.drawBitmap(mBitmapFromSdcard, 100f, 100f, mPaint) //рисуем его на нашем канвасе
        invalidate()
    }

    val funcArray = arrayOf(::drawSquare, ::drawCircle, ::drawFace, ::onSaveClick)
    fun onSaveClick() {
//получаем путь к каталогу программы на карте памяти (для этого проекта -
// /storage/emulated/0/Android/data/com.example.composeexample/files)
        val destPath: String = context.getExternalFilesDir(null)!!.absolutePath
        var outStream: OutputStream? = null //объявляем поток вывода
        val file = File(destPath, "my.PNG") //создаем файл с нужным путем и названием
        println("path = $destPath") //вывод в консоль для отладки
        outStream = FileOutputStream(file) //создаем объект потока и связываем его с файлом
//у нашего битмапа вызываем функцию для записи его с нужными параметрами (тип графического файла,
//качество в процентах и поток для записи)
        mBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        outStream.flush() //для прохождения данных вызываем функцию flush у потока
        outStream.close() //закрываем поток
    }

    //этот метод будет срабатывать при касании нашего объекта пользователем
    override fun onTouchEvent(event: MotionEvent): Boolean { //event хранит информацию о событии
        when (event.action) { // в зависимости от события
            MotionEvent.ACTION_DOWN -> { //если пользователь только коснулся объекта
                path = Path() //создаем новый объект класса Path для записи линии рисования
                path.moveTo(event.x, event.y) //перемещаемся к месту касания
            }
//если пользователь перемещает палец по экрану или отпустил палец
//проводим линию в объекте path до точки касания
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> path.lineTo(event.x,event.y)
        }
        if (path != null) { //если объект не нулевой
            println("mCanvas = $mCanvas")
            mCanvas!!.drawPath(path, mPaint!!)//рисуем на канвасе объект path (и что с ним связано)
            invalidate() //для срабатывания метода onDraw
        }
        return true
    }
}