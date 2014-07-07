import nltk.classify.util
from nltk.classify import NaiveBayesClassifier
from nltk.corpus import movie_reviews

classifier = None
feature_extractor = None

def _default_feature_extractor(words):
    return dict([(word, True) for word in words])

def classify(text):
    return classifier.classify(feature_extractor(text))

def train():
    global classifier

    # Train our classifier
    negids = movie_reviews.fileids('neg')
    posids = movie_reviews.fileids('pos')

    negfeats = [(feature_extractor(movie_reviews.words(fileids=[f])), 'neg')
                for f in negids]
    posfeats = [(feature_extractor(movie_reviews.words(fileids=[f])), 'pos')
                for f in posids]

    classifier = NaiveBayesClassifier.train(negfeats + posfeats)

feature_extractor = _default_feature_extractor
