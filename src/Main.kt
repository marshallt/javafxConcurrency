import javafx.application.Application
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.*
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.awt.*
import java.awt.image.BufferedImage
import java.lang.Thread.sleep


public class Main : Application() {
    //@Throws(Exception::class)
    var image = Image("milky-way-2695569_1280.jpg") //or use a URL
    val baseImage: BufferedImage = SwingFXUtils.fromFXImage(image, null)
    var imageView = ImageView(image)
    var scrollPane = ScrollPane(imageView)

    override fun start(primaryStage: Stage) {

        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        scrollPane.isPannable = true


        //set width and height to 75% of image to demonstrate panning
        val scene = Scene(scrollPane, image.width.toDouble() * 0.75, image.height.toDouble() * 0.75, true, SceneAntialiasing.BALANCED)

        primaryStage.scene = scene
        primaryStage.show()
        run()
       }

    fun createTask(bi: BufferedImage): Task<Image> {
        val task = object : Task<Image>() {
            @Throws(Exception::class)
            override fun call(): Image {
                sleep(1000)
                val start = System.nanoTime()
                val result = renderImage(bi)
                println("Render time: ${(System.nanoTime() - start)/1_000_000.0}ms")
                return renderImage(bi)
            }
        }

        task.onSucceeded = EventHandler<WorkerStateEvent>() {updateImage(task.get())}

        return task
    }

    fun updateImage(image: Image) {
        imageView.image = image
        run()
    }

    fun run() {
        val thread = Thread(createTask(baseImage))
        thread.isDaemon = true
        thread.start()
    }

    fun BufferedImage.copy(): BufferedImage {
        val b = BufferedImage(this.width, this.height, this.type)
        val g = b.graphics
        g.drawImage(this, 0, 0, null)
        g.dispose()
        return b
    }

    fun renderImage(baseImage: BufferedImage): Image {
        val bi = baseImage.copy()
        var g2 = bi.graphics as Graphics2D

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2.color = Color.WHITE
        g2.font = Font(Font.SANS_SERIF, Font.PLAIN, 25)
        g2.drawString(java.time.LocalTime.now().toString(), 50.0f, 50.0f)

        g2.dispose()
        return SwingFXUtils.toFXImage(bi, null)
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}