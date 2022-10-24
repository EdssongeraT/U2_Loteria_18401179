package mx.edu.ittepic.loteria_18401179

import android.media.MediaPlayer

class Juego(activity: MainActivity,indices:Array<Int>):Thread(){
    var act = activity
    val indices = indices
    var ejecutar = true
    private var pausar = false
    var i=0
    var mazoCompleto = true
    override fun run() {
        super.run()
        mazoCompleto = false
        dormir(3000)
        while (i < indices.size) {
            if (ejecutar) {
                if (!estaPausado()) {
                    pasarCartas(i)
                    dormir(3000)
                    i++
                }
            }
            if(i==indices.size-1){
                mazoCompleto = true
                ejecutar=false

            }
        }
    }
    fun pasarCartas(co:Int){
        act.runOnUiThread {
            var mediaPlayer= MediaPlayer.create(act, act.audio(co))
            act.binding.mazo.setBackgroundResource(act.imagen(co))
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }
        }
    }
    fun dormir(ti:Long){
        sleep(ti)
    }
    fun terminarHilo(){
        ejecutar = false
    }

    fun pausarHIlo(){
        pausar = true
    }

    fun despausarHilo(){
        pausar = false
    }

    fun estaPausado(): Boolean {
        return pausar
    }
}
