package com.example.demoges_parking.network

import com.example.demoges_parking.model.ApiResponseActivos
import com.example.demoges_parking.model.AuthResponse
import com.example.demoges_parking.model.CierreData
import com.example.demoges_parking.model.CierreRegistroRequest
import com.example.demoges_parking.model.CierreRegistroResponse
import com.example.demoges_parking.model.CierreRequest
import com.example.demoges_parking.model.CierreResult
import com.example.demoges_parking.model.ConsultaVehiculoResult
import com.example.demoges_parking.model.EstadoTurnoResponse
import com.example.demoges_parking.model.GenericResponseSesion
import com.example.demoges_parking.model.HistorialTurnoResult
import com.example.demoges_parking.model.IngresoData
import com.example.demoges_parking.model.IngresoRequest
import com.example.demoges_parking.model.IngresoResponse
import com.example.demoges_parking.model.LoginRequest
import com.example.demoges_parking.model.LogoutRequest
import com.example.demoges_parking.model.ResumenTurnoResponse
import com.example.demoges_parking.model.SalidaPlacaReq
import com.example.demoges_parking.model.SalidaRegistroResponse
import com.example.demoges_parking.model.SalidaReq
import com.example.demoges_parking.model.SalidaRes
import com.example.demoges_parking.model.SalidaResponseDTO
import com.example.demoges_parking.model.TarifaRequest
import com.example.demoges_parking.model.TarifaResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("auth/check-session")
    suspend fun checkSession(@Query("token") token: String): Response<Map<String, Any>>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("/cliente/prepago/{placa}")
    suspend fun consultarClientePrepago(@Path("placa") placa: String): ResponseBody

    @GET("/api/tipos-vehiculo")
    suspend fun obtenerTiposVehiculo(): List<String>

    @GET("/api/tipos-servicio")
    suspend fun obtenerTiposServicio(): List<String>

    @POST("/api/ingreso")
    suspend fun registrarIngreso(@Body request: IngresoRequest): Response<IngresoResponse>

    @GET("/api/consulta/{placa}")
    suspend fun consultarVehiculo(@Path("placa") placa: String): Response<ConsultaVehiculoResult>

    @POST("salida")
    suspend fun consultarSalidaPorPlaca(@Body request: SalidaPlacaReq): Response<SalidaRes>

    @POST("registro/salida")
    suspend fun registrarSalida(@Body request: SalidaReq): Response<SalidaRegistroResponse>

    @POST("tarifas")
    suspend fun obtenerTarifa(@Body request: TarifaRequest): Response<TarifaResponse>

    @POST("/cierre")
    suspend fun obtenerDatosTurno(@Body request: CierreRequest): Response<CierreResult>

    @POST("/cierre/registro")
    suspend fun registrarCierre(@Body cierre: CierreRegistroRequest): Response<CierreRegistroResponse>

    @POST("auth/logout")
    suspend fun cerrarSesion(@Body request: LogoutRequest): Response<GenericResponseSesion>

    @GET("api/turno/historial/{numeroTurno}")
    suspend fun obtenerHistorialTurno(@Path("numeroTurno") numeroTurno:Int): Response<HistorialTurnoResult>

    @GET("/api/vehiculos/activos")
    suspend fun obtenerVehiculosActivos(): Response<ApiResponseActivos>

    @GET("turno/resumen")
    suspend fun obtenerResumenTurno(@Query("turno") turno: Int): Response<ResumenTurnoResponse>

    @GET("api/ingreso/activo")
    suspend fun getIngresoActivo(@Query("placa") placa: String): Response<IngresoData>

    @GET("/api/turno/historial/salida/{idSalida}")
    suspend fun getDetalleSalida(@Path("idSalida") idSalida: Int): Response<SalidaResponseDTO>

    @GET("cierre/ultimoPorTurno")
    suspend fun obtenerUltimoCierrePorTurno(@Query("turno") turno: Int): Response<CierreData>

}