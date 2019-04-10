while ! nc -z mysql-database 3306;
        do
          echo sleeping;
          sleep 1;
        done;
        echo Connected!;
catalina.sh run
