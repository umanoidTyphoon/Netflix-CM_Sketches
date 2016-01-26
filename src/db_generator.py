__author__ = 'umanoidTyphoon'

import __init__ as init


class DbGenerator:
    def __init__(self):
        self.connection        = init.connect_to_db()
        self.connection_cursor = self.connection.cursor()

    def create_from_random_users_file(self, rand_usr_file, out_file):
        RATING_POSITION = 2
        DATE_POSITION   = 3

        query = "SELECT MAX(movieid) FROM movies"
        self.connection_cursor.execute(query)

        movie_total = self.connection_cursor.fetchone()[0]
        with open(rand_usr_file, 'r') as usr_file:
            with open(out_file, 'w') as csv_file:
                csv_writer = init.csv.writer(csv_file)
                user_ids   = map(int, usr_file.readlines())

                for user_id in user_ids:
                    print "DB GENERATOR :: Processing user %d" % user_id
                    for movie_id in range(movie_total):
                        movie_id += 1

                        query = "SELECT * FROM training_set WHERE customerid = " + str(user_id) + " AND movieid = " \
                                + str(movie_id)

                        self.connection_cursor.execute(query)
                        query_result = self.connection_cursor.fetchone()

                        if query_result is None:
                            continue

                        date   = query_result[DATE_POSITION]
                        rating = query_result[RATING_POSITION]

                        csv_writer.writerow([movie_id, user_id, rating, date])