package com.example.projetokotlin.view.inicioEmpresa.relatorioOrdem

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityRelatorioDasOrdensBinding
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.listaServico.MyAdapter
import com.example.projetokotlin.view.listaServico.Ordem
import com.example.projetokotlin.view.listaServico.uitel.LoadingDialog
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RelatorioDasOrdens : AppCompatActivity() {
    private lateinit var binding: ActivityRelatorioDasOrdensBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var TextViewVoltar: TextView
    private lateinit var servicoList: ArrayList<Ordem>
    private var filtro: String = ""
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelatorioDasOrdensBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Inicializa o botão para gerar PDF
        val btnPdf: Button = findViewById(R.id.btn_pdf)
        btnPdf.setOnClickListener {
            if (filtro == "Finalizado" || filtro == "Cancelado") {
                gerarPdf(servicoList)  // Passa a lista de ordens para a função de geração do PDF
            } else {
                Toast.makeText(this, "Selecione 'Finalizado' ou 'Cancelado' para gerar o PDF.", Toast.LENGTH_SHORT).show()
            }
        }

        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed({
            loading.isDismiss()
        }, 2000)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        servicoList = arrayListOf()

        TextViewVoltar = findViewById(R.id.voltar)
        TextViewVoltar.setOnClickListener {
            telaInicial()
        }

        // Opções do dropdown
        val item = listOf("Finalizado", "Cancelado")
        val autoComplete: AutoCompleteTextView = findViewById(R.id.auto_completeText)
        val adapter = ArrayAdapter(this, R.layout.list_item, item)
        autoComplete.setAdapter(adapter)

        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "Item $itemSelected", Toast.LENGTH_SHORT).show()
            filtro = itemSelected.toString()
            servicoList.clear()
            buscarParaEmpresa()
        }
        buscarParaEmpresa()
    }

    private fun buscarParaEmpresa() {
        db.collection("Servico")
            .get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (data in documents) {
                        val servico: Ordem? = data.toObject(Ordem::class.java)
                        if (servico != null && (servico.status.toString() == "Finalizado" || servico.status.toString() == "Cancelado")) {
                            if (filtro != "" && filtro == servico.status) {
                                servicoList.add(servico)
                            } else if (filtro == "") {
                                servicoList.add(servico)
                            }
                        }
                    }
                    recyclerView.adapter = RelatorioAdapter(servicoList, this)
                } else {
                    mensagem("Não existe registros para exibir, volte para tela inicial", "AVISO")
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    // Função para gerar o PDF
    private var pdfCount = 0  // Contador de PDFs gerados

    fun gerarPdf(servicoList: List<Ordem>) {
        pdfCount++  // Incrementa o contador
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        var yPosition = 40f

        for (servico in servicoList) {
            canvas.drawText("Cliente: ${servico.Cliente ?: "Não informado"}", 20f, yPosition, paint)
            canvas.drawText("Descrição: ${servico.descricao ?: "Não informado"}", 20f, yPosition + 20, paint)
            canvas.drawText("Valor: ${servico.valor ?: "Não informado"}", 20f, yPosition + 40, paint)
            canvas.drawText("Status: ${servico.status ?: "Não informado"}", 20f, yPosition + 60, paint)
            canvas.drawText("Porte do Sistema: ${servico.porteSistema ?: "Não informado"}", 20f, yPosition + 80, paint)
            canvas.drawText("ID: ${servico.id ?: "Não informado"}", 20f, yPosition + 100, paint)

            yPosition += 140
        }

        pdfDocument.finishPage(page)

        // Define o caminho para salvar o PDF com o contador
        val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "RelatorioOrdem ($pdfCount).pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            Log.d("PDF", "PDF gerado com sucesso em: ${filePath.absolutePath}")
            Toast.makeText(this, "PDF salvo em: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("PDF", "Erro ao salvar PDF: ${e.message}", e)
            Toast.makeText(this, "Erro ao salvar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }



    private fun mensagem(msg: String, titulo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK") { _, _ -> telaInicial() }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun telaInicial() {
        val email = FirebaseAuth.getInstance().currentUser
        email?.let {
            if (email.email == "devplacemobile@gmail.com") {
                val intent = Intent(this, telaInicialEmpresa::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, telaNavegacao::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
