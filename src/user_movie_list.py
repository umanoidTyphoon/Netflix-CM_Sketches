__author__ = 'umanoidTyphoon'

import __init__ as init


class UserMovieListGen:
    def __init__(self):
        self.connection        = init.connect_to_db()
        self.connection_cursor = self.connection.cursor()

    def generate_user_list(self, out_file):
        user_list = []
        query     = "SELECT DISTINCT customerid FROM training_set"

        self.connection_cursor.execute(query)
        for row in self.connection_cursor:
            customer_id = row[0]
            user_list.append(customer_id)

        sorted_user_list = sorted(user_list)

        with open(out_file, 'w') as user_file:
            for user in sorted_user_list:
                user_file.write(str(user) + '\n')

    def generate_movie_list(self, out_file):
        movie_list = []
        query      = "SELECT movieid FROM movies"

        self.connection_cursor.execute(query)
        for row in self.connection_cursor:
            movie_id = row[0]
            movie_list.append(movie_id)

        sorted_movie_list = sorted(movie_list)

        with open(out_file, 'w') as movie_file:
            for movie in sorted_movie_list:
                movie_file.write(str(movie) + '\n')

    def pick_random_users(self, user_file, m_users):
        with open(user_file, 'r') as usr_file:
            users        = map(int, usr_file.readlines())
            random_users = init.random.sample(users, m_users)

        return sorted(random_users)

    def pick_random_movies(self, movie_file, m_movies):
        with open(movie_file, 'r') as mov_file:
            movies        = map(int, mov_file.readlines())
            random_movies = init.random.sample(movies, m_movies)

        return sorted(random_movies)

    def write_random_users_on_file(self, random_users, out_filename):
        out_filename  = out_filename.split(".txt")[0] + "_"
        out_filename += init.datetime.datetime.fromtimestamp(init.time.time()).strftime('%Y-%m-%d_%H-%M-%S') + ".txt"

        with open(out_filename, 'w') as user_file:
            for user in random_users:
                user_file.write(str(user) + '\n')

    def write_random_movies_on_file(self, random_movies, out_filename):
        out_filename  = out_filename.split(".txt")[0] + "_"
        out_filename += init.datetime.datetime.fromtimestamp(init.time.time()).strftime('%Y-%m-%d_%H-%M-%S') + ".txt"

        with open(out_filename, 'w') as movie_file:
            for movie in random_movies:
                movie_file.write(str(movie) + '\n')