__author__ = 'umanoidTyphoon'

from src.user_movie_list import UserMovieListGen

import __init__ as init

if __name__ == '__main__':
    m_users      = int(init.TOTAL_NETFLIX_DATASET_USERS * init.PUBLISHED_RECORDS_RATIO) + 1
    out_filename = "../datasets/Netflix_prize_dataset/download/users.txt"
    # out_file     = open(out_filename, "w")
    user_list_generator = UserMovieListGen()

    random_users = user_list_generator.pick_random_users(out_filename, m_users)
    user_list_generator.write_random_users_on_file(random_users, out_filename)

    # user_list_generator.generate_movie_list(out_filename)
    # random_movies = user_list_generator.pick_random_movies(out_filename, 10000)
    # user_list_generator.write_random_users_on_file(random_movies, out_filename)