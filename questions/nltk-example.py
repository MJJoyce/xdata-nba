# This is not my original work. This was taken (mostly) wholesale from
# http://streamhacker.com/2010/05/10/text-classification-sentiment-analysis-naive-bayes-classifier/

import nltk.classify.util
from nltk.classify import NaiveBayesClassifier
from nltk.corpus import movie_reviews

TEST_COMMENT_FILE = '../test-data/comments/0021300632_espn_comments.txt'
 
def word_feats(words):
    return dict([(word, True) for word in words])
 
negids = movie_reviews.fileids('neg')
posids = movie_reviews.fileids('pos')
 
negfeats = [(word_feats(movie_reviews.words(fileids=[f])), 'neg') for f in negids]
posfeats = [(word_feats(movie_reviews.words(fileids=[f])), 'pos') for f in posids]
 
poscutoff = len(posfeats)
negcutoff = len(negfeats)
 
trainfeats = negfeats[:negcutoff] + posfeats[:poscutoff]
testfeats = negfeats[negcutoff:] + posfeats[poscutoff:]
 
classifier = NaiveBayesClassifier.train(trainfeats)

with open(TEST_COMMENT_FILE) as comment_file:
    for line in comment_file:
        user, comment = line.split('::')
        print '-------'
        #print classifier.classify(feature_extractor(comment)), comment
        print classifier.classify(word_feats(comment)), comment
