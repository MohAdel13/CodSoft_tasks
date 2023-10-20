package com.example.calculator

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding
import org.mozilla.javascript.Context

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.formulaET.showSoftInputOnFocus = false
        binding.formulaET.text.append("0")

        binding.num0BTN.setOnClickListener{
            updateText("0")
        }

        binding.num1BTN.setOnClickListener{
            updateText("1")
        }

        binding.num2BTN.setOnClickListener{
            updateText("2")
        }

        binding.num3BTN.setOnClickListener{
            updateText("3")
        }

        binding.num4BTN.setOnClickListener{
            updateText("4")
        }

        binding.num5BTN.setOnClickListener{
            updateText("5")
        }

        binding.num6BTN.setOnClickListener{
            updateText("6")
        }

        binding.num7BTN.setOnClickListener{
            updateText("7")
        }

        binding.num8BTN.setOnClickListener{
            updateText("8")
        }

        binding.num9BTN.setOnClickListener{
            updateText("9")
        }

        binding.openBracketBTN.setOnClickListener{
            updateText("(")
        }

        binding.closeBracketBTN.setOnClickListener{
            updateText(")")
        }

        binding.divideBTN.setOnClickListener{
            updateText("/")
        }

        binding.multiplyBTN.setOnClickListener{
            updateText("*")
        }

        binding.plusBTN.setOnClickListener{
            updateText("+")
        }

        binding.minusBTN.setOnClickListener{
            updateText("-")
        }

        binding.dotBTN.setOnClickListener{
            updateText(".")
        }

        binding.clearBTN.setOnClickListener{
            binding.formulaET.setText("")
            binding.formulaET.append("0")
            binding.resultTV.text = "0"
        }

        binding.equalBTN.setOnClickListener{
            val data = binding.formulaET.text.toString()
            var result = getResult(data)

            if(result == "Error")
            {
                binding.resultTV.text = "Syntax Error"
            }

            else if((result == "Infinity") || (result == "-Infinity"))
            {
                binding.resultTV.text = "Math Error"
            }

            else
            {
                if(result.endsWith(".0"))
                {
                    result = result.replace(".0","")
                }
                binding.resultTV.text = result
            }
        }

        binding.backBTN.setOnClickListener{
            val cursorPos = binding.formulaET.selectionStart
            val formulaSize = binding.formulaET.text.length
            if((cursorPos != 0) && (formulaSize != 0))
            {
                val selection = binding.formulaET.text as (SpannableStringBuilder)
                selection.replace(cursorPos - 1, cursorPos, "")
                binding.formulaET.text = selection
                binding.formulaET.setSelection(cursorPos - 1)
            }
        }

    }

    private fun updateText(strToAdd: String)
    {
        val oldStr = binding.formulaET.text.toString()
        val cursorPos = binding.formulaET.selectionStart
        val leftStr = oldStr.substring(0,cursorPos)
        val rightStr = oldStr.substring(cursorPos)
        if(binding.formulaET.text.toString() == "0" &&
            (strToAdd[0].isDigit() || strToAdd == "(" || strToAdd ==")"))
        {
                binding.formulaET.setText("")
                binding.formulaET.append(strToAdd)
        }
        else {
            binding.formulaET.setText(String.format("%s%s%s", leftStr, strToAdd, rightStr))
            binding.formulaET.setSelection(cursorPos + 1)
        }
    }

    private fun getResult(data: String): String {
        return try {
            val context = Context.enter()
            context.optimizationLevel = -1
            val scriptable = context.initStandardObjects()
            context.evaluateString(
                scriptable, data, "Javascript", 1,
                null
            ).toString()
        } catch (e: Exception) {
            "Error"
        }
    }
}