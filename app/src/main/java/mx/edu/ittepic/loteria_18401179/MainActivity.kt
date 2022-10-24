package mx.edu.ittepic.loteria_18401179

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mx.edu.ittepic.loteria_18401179.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var indices = Array<Int>(54) { i -> i + 1 }
    var j=Juego(this,indices)
    lateinit var mp: MediaPlayer
    var indice=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.terminar.isEnabled=false
        binding.pausar.isEnabled=false

        binding.barajear.setOnClickListener{
            barajear()
        }

        binding.iniciar.setOnClickListener{
            //barajeado inicial para que no siempre empiece en orden
            barajear()
            //si el juego anterior sigue corriendo en la fase de comprobacion
            //el boton iniciar solo mandara un mensaje que diga espera
            if(j.mazoCompleto) {
                setTitle("juego en curso")

                j.mazoCompleto = false

                binding.iniciar.isEnabled = false
                binding.barajear.isEnabled = false
                binding.pausar.isEnabled = true

                if (!j.ejecutar) {
                    j.i = 0
                    j.ejecutar = true
                    j.despausarHilo()
                } else if (j.ejecutar) {
                    mp = MediaPlayer.create(this, R.raw.inicio)
                    mp.start()
                    j.start()
                    j.dormir(3000)
                }
            }else{
                setTitle("espera a que termine el juego anterior")
            }
        }

        binding.pausar.setOnClickListener{
            if (j.estaPausado()){
                j.despausarHilo()
                binding.terminar.isEnabled=false
                binding.pausar.setText("¡¡LOTERIA!!")
            }else if(!j.estaPausado()){
                j.pausarHIlo()
                binding.terminar.isEnabled=true
                binding.pausar.setText("Se equivoco")
            }
        }

        binding.terminar.setOnClickListener {
            setTitle("comprobando cartas")
            binding.pausar.isEnabled = false
            binding.pausar.setText("¡¡LOTERIA!!")
            binding.terminar.isEnabled = false
            j.terminarHilo()
            if (j.mazoCompleto) {
                mp= MediaPlayer.create(this, R.raw.completo)
                activar()
                mp.start()

            } else {
                indice = j.i + 1
                binding.barajear.isEnabled = false
                mp = MediaPlayer.create(this, R.raw.fin)
                mp.start()
                mp.setOnCompletionListener { mp.release() }


                val corutina = GlobalScope.launch {
                    delay(5000)
                    while (indice < indices.size) {
                        var mpl= MediaPlayer.create(this@MainActivity, audio(indice))
                        this@MainActivity.runOnUiThread {
                            binding.mazo.setBackgroundResource(imagen(indice))
                        }
                        mpl.start()
                        mpl.setOnCompletionListener { mpl.release()}
                        delay(3000)
                        indice++
                        if(indice==indices.size-1){
                            j.mazoCompleto =true
                        }
                    }
                }

                activar()
            }
        }
    }

    fun barajear(){
        for (i in indices.size - 1 downTo 1){
            val j = Random.nextInt(i+1)
            val temp = indices[i]
            indices[i] = indices[j]
            indices[j] = temp
            print(temp.toString()+",")
        }
    }
    fun activar(){
        binding.barajear.isEnabled=true
        binding.iniciar.isEnabled=true
    }

    fun imagen(id: Int): Int {
        var nombre = "carta${indices[id]}"
        return resources.getIdentifier(nombre, "drawable", packageName)
    }

    fun audio(id: Int): Int {
        var nombre = "a${indices[id]}"
        return resources.getIdentifier(nombre, "raw", packageName)
    }
}