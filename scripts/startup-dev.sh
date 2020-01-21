while ! nc -z $1 $2;
        do
          echo sleeping;
          sleep 1;
        done;
        echo Connected!;
catalina.sh run
