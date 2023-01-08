package ru.khozyainov.splashun.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import ru.khozyainov.splashun.databinding.FragmentLoginBinding
import ru.khozyainov.splashun.presentation.MainActivity
import ru.khozyainov.splashun.utils.ViewBindingFragment
import ru.khozyainov.splashun.utils.launchAndCollect

@AndroidEntryPoint
class LoginFragment: ViewBindingFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

    //Открываем активити по интенту. Нам необходимо обработать результат активити, чтобы получить код
    private val getAuthResponse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val dataIntent = activityResult.data ?: return@registerForActivityResult
        handleAuthResponseIntent(dataIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind()

        binding.singInButton.setOnClickListener {
            viewModel.openLoginPage()
        }
    }

    private fun bind(){
        viewModel.openAuthPageFlow.launchAndCollect(viewLifecycleOwner){ intent ->
            openAuthPage(intent)
        }

        viewModel.loginIsDone.launchAndCollect(viewLifecycleOwner) { loginIDone ->
            if (loginIDone) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun openAuthPage(intent: Intent) {
        getAuthResponse.launch(intent)
    }

    //Нужно получить код и обменять его на токен.
    //При этом в ответе может прийти ошибка авторизации, ее тоже нужно обработать.
    private fun handleAuthResponseIntent(intent: Intent) {
        // пытаемся получить ошибку из ответа. null - если все ок
        val exception = AuthorizationException.fromIntent(intent)
        // пытаемся получить запрос для обмена кода на токен, null - если произошла ошибка
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            // авторизация завершались ошибкой
            exception != null -> viewModel.onAuthFailed(exception)
            // авторизация прошла успешно, меняем код на токен
            tokenExchangeRequest != null ->
                viewModel.getTokenByRequest(tokenExchangeRequest)
        }
    }

}