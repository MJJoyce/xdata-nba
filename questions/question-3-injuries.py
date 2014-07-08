SOLR_URL = 'http://localhost:8983/solr/'
PLAYER_STATS_CORE = 'game-player-stats/'
INJURY_QUERY = '?&wt=json&indent=true&rows=214749364&group=true&group.field=player_name&group.limit=100'

q_words = ['injury', 'fracture', 'sprain', 'strain', 'sore', 'pain']
q_fields = ['comment'] * len(q_words)
query = [a + ':' + b for a, b in zip(q_fields, q_words)]
injury_query = INJURY_QUERY + '&q=' + '%0A'.join(query)

fq_words = ['personal', 'suspended', 'suspension', 'trade', 'NBADL', 'NBDL',
            'cold', 'flu', 'illness', 'conditioning', 'family', 'gastric',
            'sick', 'soreness', 'virus', 'viral', 'poisoning', 'organizational',
            'personnel', 'today', 'dental', 'illness', 'precautionary',
            'migraine', 'migrane']
fq_fields = ['-comment'] * len(fq_words)
filter_query = [a + ':' + b for a, b in zip(fq_fields, fq_words)]
filter_query = ''.join([a + b for a, b in zip(['&fq='] * len(filter_query), filter_query)])
#injury_query += filter_query
#injury_query += ''.join([a + b for a, b in zip(['&fq='] * len(filter_query), filter_query)])

injury_query = SOLR_URL + PLAYER_STATS_CORE + 'select/' + injury_query
print injury_query

print '----'

pot_q_words = ['NWT', 'DNP', 'DND']
pot_q_fields = ['comment'] * len(pot_q_words)
pot_query = [a + ':' + b for a, b in zip(pot_q_fields, pot_q_words)]
pot_query = '&q=' + '%0A'.join(pot_query)
pot_injury_query = INJURY_QUERY + pot_query + filter_query
pot_injury_query = SOLR_URL + PLAYER_STATS_CORE + 'select/' + pot_injury_query

print pot_injury_query
