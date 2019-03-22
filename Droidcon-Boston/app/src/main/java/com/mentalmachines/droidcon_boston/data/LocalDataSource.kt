package com.mentalmachines.droidcon_boston.data

import android.content.Context
import com.mentalmachines.droidcon_boston.modal.Media
import com.mentalmachines.droidcon_boston.modal.QuotedTweet
import com.mentalmachines.droidcon_boston.modal.Tweet
import com.mentalmachines.droidcon_boston.modal.TweetWithMedia

class LocalDataSource private constructor(private val appDatabase: AppDatabase) : DataSource {

    companion object {
        private lateinit var dataSource: LocalDataSource
        fun getInstance(context: Context): LocalDataSource {
            if (!::dataSource.isInitialized) {
                dataSource = LocalDataSource(AppDatabase.getInstance(context))
            }
            return dataSource
        }
    }

    override fun getTweets(): List<TweetWithMedia> {
        return appDatabase.tweetDao().getTweets()
    }

    fun updateTweets(tweets: List<TweetWithMedia>) {
        segregateEntity(tweets)
    }

    private fun segregateEntity(tweetsWithMedia: List<TweetWithMedia>) {
        val tweets: MutableList<Tweet> = arrayListOf()
        val quotedTweet: MutableList<QuotedTweet> = arrayListOf()
        val media: MutableList<Media> = arrayListOf()

        tweetsWithMedia.forEach { tweetWithMedia ->
            tweets.add(tweetWithMedia.tweet)
            tweetWithMedia.media?.let { media.addAll(it) }
            tweetWithMedia.quotedMedia?.let { media.addAll(it) }
            tweetWithMedia.tweet.quotedTweet?.let { quotedTweet.add(it) }
        }

        if (!tweets.isEmpty()) {
            appDatabase.tweetDao().updateTweets(tweets)
        }

        if (!quotedTweet.isEmpty()) {
            appDatabase.quotedTweetDao().updateTweets(quotedTweet)
        }

        if (!media.isEmpty()) {
            appDatabase.mediaDao().insertAll(media)
        }
    }
}
