package com.tienda.quirquincho.data.almacenamiento

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

/**
 * Manager para almacenar y recuperar el token JWT de forma segura
 */
class TokenManager(private val context: Context) {

    companion object {
        // Nombre del DataStore
        private const val NOMBRE_DATASTORE = "token_preferences"

        // Keys para los valores almacenados
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USUARIO_ID = stringPreferencesKey("usuario_id")
        private val KEY_NOMBRE_USUARIO = stringPreferencesKey("nombre_usuario")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_NOMBRE_COMPLETO = stringPreferencesKey("nombre_completo")
        private val KEY_EXPIRA_EN = stringPreferencesKey("expira_en")

        // Extension para crear DataStore
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = NOMBRE_DATASTORE
        )
    }

    /**
     * Guarda los datos de sesión del usuario
     */
    suspend fun guardarDatosSesion(
        token: String,
        usuarioId: String,
        nombreUsuario: String,
        email: String,
        nombreCompleto: String,
        expiraEn: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            preferences[KEY_USUARIO_ID] = usuarioId
            preferences[KEY_NOMBRE_USUARIO] = nombreUsuario
            preferences[KEY_EMAIL] = email
            preferences[KEY_NOMBRE_COMPLETO] = nombreCompleto
            preferences[KEY_EXPIRA_EN] = expiraEn
        }
    }

    /**
     * Obtiene el token JWT almacenado
     */
    fun obtenerToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_TOKEN]
        }
    }

    /*
     * Obtiene el token JWT almacenado de forma síncrona
     */
    suspend fun obtenerTokenSincrono(): String? {
        val token = context.dataStore.data.map { preferences ->
            preferences[KEY_TOKEN]
        }.first()

        return if (token?.startsWith("Bearer ") == true) {
            token
        } else if (token != null) {
            "Bearer $token"
        } else {
            null
        }
    }

    /**
     * Obtiene los datos del usuario almacenados
     */
    fun obtenerDatosUsuario(): Flow<DatosUsuarioAlmacenado?> {
        return context.dataStore.data.map { preferences ->
            val token = preferences[KEY_TOKEN]
            val usuarioId = preferences[KEY_USUARIO_ID]
            val nombreUsuario = preferences[KEY_NOMBRE_USUARIO]
            val email = preferences[KEY_EMAIL]
            val nombreCompleto = preferences[KEY_NOMBRE_COMPLETO]
            val expiraEn = preferences[KEY_EXPIRA_EN]

            if (token != null && usuarioId != null && nombreUsuario != null) {
                DatosUsuarioAlmacenado(
                    token = token,
                    usuarioId = usuarioId,
                    nombreUsuario = nombreUsuario,
                    email = email ?: "",
                    nombreCompleto = nombreCompleto ?: "",
                    expiraEn = expiraEn ?: ""
                )
            } else {
                null
            }
        }
    }

    /**
     * Verifica si el usuario está logueado
     */
    fun estaLogueado(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_TOKEN] != null
        }
    }

    /**
     * Limpia todos los datos de sesión (logout)
     */
    suspend fun limpiarSesion() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

/**
 * Data class para los datos del usuario almacenados
 */
data class DatosUsuarioAlmacenado(
    val token: String,
    val usuarioId: String,
    val nombreUsuario: String,
    val email: String,
    val nombreCompleto: String,
    val expiraEn: String
)