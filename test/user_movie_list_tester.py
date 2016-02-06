__author__ = 'umanoidTyphoon'

from src.user_movie_list import UserMovieListGen

if __name__ == '__main__':
    out_filename = "../datasets/Netflix_prize_dataset/download/movies.txt"
    out_file     = open(out_filename, "w")
    user_list_generator = UserMovieListGen()

    user_list_generator.generate_movie_list(out_filename)
    # random_users = user_list_generator.pick_random_users(out_filename, 10000)
    # user_list_generator.write_random_users_on_file(random_users, out_filename)

    random_movies = user_list_generator.pick_random_movies(out_filename, 10000)
    user_list_generator.write_random_users_on_file(random_movies, out_filename)

    print random_movies