__author__ = 'umanoidTyphoon'

import __init__ as init


class UserListGen:
    def __init__(self):
        self.connection        = init.connect_to_db()
        self.connection_cursor = self.connection.cursor()

    def generate_list(self, out_file):
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

    def pick_random_users(self, user_file, m_users):
        with open(user_file, 'r') as usr_file:
            users        = map(int, usr_file.readlines())
            random_users = init.random.sample(users, m_users)

        return sorted(random_users)

    def write_random_users_on_file(self, random_users, out_filename):
        out_filename  = out_filename.split(".txt")[0] + "_"
        out_filename += init.datetime.datetime.fromtimestamp(init.time.time()).strftime('%Y-%m-%d_%H-%M-%S') + ".txt"

        with open(out_filename, 'w') as user_file:
            for user in random_users:
                user_file.write(str(user) + '\n')