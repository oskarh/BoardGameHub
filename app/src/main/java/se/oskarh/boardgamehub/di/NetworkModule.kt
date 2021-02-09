package se.oskarh.boardgamehub.di

import android.content.Context
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.oskarh.boardgamehub.BuildConfig
import se.oskarh.boardgamehub.repository.BoardGameGeekService
import se.oskarh.boardgamehub.repository.LiveDataCallAdapterFactory
import se.oskarh.boardgamehub.repository.RedditService
import se.oskarh.boardgamehub.repository.YouTubeService
import se.oskarh.boardgamehub.repository.converter.HtmlEscapeStringConverter
import se.oskarh.boardgamehub.util.BOARDGAMEGEEK_BASE_URL
import se.oskarh.boardgamehub.util.REDDIT_BASE_URL
import se.oskarh.boardgamehub.util.YOUTUBE_BASE_URL

@Module
object NetworkModule {

    @JvmStatic
    @Provides
    @Reusable
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BASIC
                else
                    HttpLoggingInterceptor.Level.NONE
        }

    @JvmStatic
    @Provides
    @Reusable
    fun provideOkHttpClient(context: Context, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
//            .addInterceptor(ChuckInterceptor(context))
//            .addInterceptor(ChuckInterceptor(context))
            .build()

    @JvmStatic
    @Provides
    @Reusable
    fun provideFinderService(okHttpClient: OkHttpClient): BoardGameGeekService =
        Retrofit.Builder()
            .baseUrl(BOARDGAMEGEEK_BASE_URL)
            .addConverterFactory(
                TikXmlConverterFactory.create(
                    TikXml.Builder()
                        .exceptionOnUnreadXml(false)
                        .addTypeConverter(String::class.java, HtmlEscapeStringConverter())
                        .build()
                )
            )
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(BoardGameGeekService::class.java)

    @JvmStatic
    @Provides
    @Reusable
    fun provideYouTubeService(okHttpClient: OkHttpClient): YouTubeService =
        Retrofit.Builder()
            .baseUrl(YOUTUBE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(YouTubeService::class.java)

    @JvmStatic
    @Provides
    @Reusable
    fun provideRedditService(okHttpClient: OkHttpClient): RedditService =
        Retrofit.Builder()
            .baseUrl(REDDIT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(RedditService::class.java)
}