package com.zafin.twitter.twitterreadapi;

import java.awt.Window.Type;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Document;
import javax.xml.crypto.Data;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zafin.twitter.dto.TweetData;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;

import java.lang.Object;
import java.math.BigInteger;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Properties;

import java.util.StringTokenizer;

class TweetReader {
	// search tweet based on a keyword
	
	
	public static void main(String[] args) throws IOException, TwitterException, InterruptedException {

		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true);
	
		 final String keywords[] = { "HDFC", "ICICI","ENBD","NBAD","HSBC"};


		// set the key to connect with a third party

		cb.setOAuthConsumerKey("KShoNCgTxYWOtDkNftYNUYScK");
		cb.setOAuthConsumerSecret("OQqUqQFoJHnfb0bx5MKwQtrBSmF0iAQzXe1I5wJgiG83t6453o");
		cb.setOAuthAccessToken("881909923176562689-FFZxp9XpOVGf7aui822X44mNEqp8AmW");
		cb.setOAuthAccessTokenSecret("PFfUsXrasLdsris1z6iGo5n5Gi4d36diDKESdZmtJNgBU");

		// listener to listen a real time tweet
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status statues) {
				// System.out.println("@" + status.getUser().getScreenName() + "
				// - " + status.getText());
				
				TweetData data = new TweetData();

				// set the properties to intialize a pipeline
				TweetReader sa = new TweetReader();
				Properties properties = new Properties();
				properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
				StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

				List<TweetData> tweetList = new ArrayList<>();
				// for (Status statues :result.getTweets()) {
				HashSet<String> usedIds = new HashSet<String>();

				// System.out.println(statues.getId());

				data.setTweetId("" + statues.getId());
				data.setUserName(statues.getUser().getName());
				data.setTweetLanguage(statues.getLang());
				data.setTweetcreatedAt("" + statues.getCreatedAt());
				data.setTweetLocation(statues.getUser().getLocation());
				data.setTweetText(statues.getText());
			for(int i=0;i<keywords.length;i++){	
				if(statues.getText().toLowerCase().contains(keywords[i].toLowerCase())){
					data.setKeyword(keywords[i]);
				
				}
			}
			
				data.setFavoriteCount(statues.getFavoriteCount());
				data.setRetweetcount(statues.getRetweetCount());
				data.setInReplyToStatusId(statues.getInReplyToStatusId());
				data.setInReplyToUserId("" + statues.getInReplyToUserId());

				data.setHashtagEntities(new String());
				statues.getHashtagEntities();
				data.setGeolocation(statues.getGeoLocation());
				data.setFollowercount(statues.getUser().getFollowersCount());
				data.setFriendcount(statues.getUser().getFriendsCount());

				data.setUrlEntities(new String());
				statues.getURLEntities();

				data.setUserMentionEntities(new String());
				statues.getUserMentionEntities();

				data.setEmail(statues.getUser().getEmail());
				data.setSource(statues.getSource());
				data.setCountrycode(statues.getUser().getWithheldInCountries());
			

				// call a method to clean and find sentiment of tweet
				String cleanedText;
				try {
					cleanedText = dataClean(data.getTweetText());
					data.setProcessToText(cleanedText);
					String sentiment = findSentiment(cleanedText, pipeline);
					data.setSentiment(sentiment);

				} catch (IOException e) {
					e.printStackTrace();
				}

				// check for the duplicate tweet
				if (!usedIds.contains(data.getTweetId())) {
					usedIds.add(data.getTweetId());
					// insert the tweet into Mongo DB
					Mongodata mongoData = new Mongodata("SMA", "127.0.0.1", 27017);
					String mongoId = mongoData.add("BANK_DATA", new Gson().toJson(data));
				}
				// Thread.sleep(2000);

			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onStallWarning(StallWarning sw) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};
		
		FilterQuery fq = new FilterQuery();
		fq.track(keywords);

		twitterStream.addListener(listener);
		twitterStream.filter(fq);
		Thread.sleep(2000);
		twitterStream.cleanUp();
		twitterStream.shutdown();

	}

	// clean a data set to remove stopwords,hashtag and url

	public static String dataClean(String TweetText) throws IOException {

		String tweetWithoutHash;// = TweetText.replaceAll("#", "");
		//tweetWithoutHash = tweetWithoutHash.replaceAll("@", "");

		String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		String tweetWithoutHashAndUrl = TweetText.replaceAll(urlPattern, "");

		tweetWithoutHashAndUrl = tweetWithoutHashAndUrl.replaceAll("[\\-\\+\\.\\^:,]", "");

		// StringTokenizer tokenizer = new
		// StringTokenizer(tweetWithoutHashAndUrl);
		String[] array = tweetWithoutHashAndUrl.split("\\s{1,}");
		String finalLine = "";
		for (String s : array) {
			if (s.contains("!") || s.contains("#") || s.contains("@")) {
				
			//	s=s.replaceAll("@[^@]*@", "");

				// s=s.replace(s,"");
			} else {

				finalLine = finalLine.concat(s + " ");

			}
		}
		finalLine = finalLine.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
		tweetWithoutHashAndUrl = finalLine.substring(0, finalLine.length() - 1);
		 Pattern p =
		Pattern.compile("\\b(a|I|this|its|'s|!|for|the|about|above|after|again|against|the|all|am|an|and|any|are|aren't|as|at|be|because|been|before|being|below|between|both|but|by|can't|cannot|could|couldn't|did|didn't|do|does|doesn't|doing|don't|down|during|each|few|for|from|further|had|hadn't|has|hasn't|have|haven't|having|he|he'd|he'll|he's|her|here|here's|hers|herself|him|himself|his|how|how's|i|i'd|i'll|i'm|i've|if|in|into|is|isn't|it|it's|its|itself|let's|me|more|most|mustn't|my|myself|no|nor|not|of|off|on|once|only|or|other|ought|our|ours|ourselves|out|over|own|same|shan't|she|she'd|she'll|she's|should|shouldn't|so|some|such|than|that|that's|the|their|theirs|them|themselves|then|there|there's|these|they|they'd|they'll|they're|they've|this|those|through|too|too|under|until|up|very|was|wasn't|we|we'd|we'll|we're|we've|were|weren't|what|what's|when|when's|where|where's|which|while|who|who's|whom|why|why's|with|won't|would|wouldn't|you|you'd|you'll|you're|you've|your|yours|yourselfyourselves)\\b\\s?");
		 Matcher m = p.matcher(tweetWithoutHashAndUrl);
		 tweetWithoutHashAndUrl = m.replaceAll("");
		

		return tweetWithoutHashAndUrl;

	}

	// code to find the sentiment of a tweet
	private static String findSentiment(String input, StanfordCoreNLP pipeline) {

		int mainSentiment = 2;
		int longest;
		if (input != null && input.length() > 0) {
			longest = 0;
			Annotation annotation = pipeline.process(input);
			// for (Object sentence : (List)
			// annotation.get(CoreAnnotations.SentencesAnnotation.class)) {

			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {

				Tree tree = (Tree) ((CoreMap) sentence).get(SentimentCoreAnnotations.AnnotatedTree.class);

				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();
				if (partText.length() > longest) {
					mainSentiment = sentiment;
					longest = partText.length();
				}
			}
		}
		if (mainSentiment <= 1) {
			return "negative";
		} else if (mainSentiment == 2) {
			return "neutral";
		} else {
			return "positive";
		}
	}

}
