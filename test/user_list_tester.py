__author__ = 'umanoidTyphoon'

from src.user_list import UserListGen

if __name__ == '__main__':
    out_filename = "../datasets/Netflix_prize_dataset/download/users.txt"
    # out_file     = open(out_filename, "w")
    user_list_generator = UserListGen()

    # user_list_generator.generate_list(out_filename)
    random_users = user_list_generator.pick_random_users(out_filename, 10000)
    user_list_generator.write_random_users_on_file(random_users, out_filename)

    print random_users