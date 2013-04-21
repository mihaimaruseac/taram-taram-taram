#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>
#include <arpa/inet.h>

#define TO 4

int main(int argc, char *argv[])
{
    struct sockaddr_rc addr = { 0 };
    int s, status;
    int value;
    char dest[18] = "00:16:53:09:B4:BB";

    // allocate a socket
    s = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);

    // set the connection parameters (who to connect to)
    addr.rc_family = AF_BLUETOOTH;
    addr.rc_channel = (uint8_t) 1;
    str2ba( dest, &addr.rc_bdaddr );

    // connect to server
    status = connect(s, (struct sockaddr *)&addr, sizeof(addr));

    // send a message
    if( status == 0 ) {
        int to = TO;
        do
        {
            scanf("%d", &value);
            int val = value;
            value = htonl(value);
            printf("val: %d htonl: %d\n", val, value);
            printf("Written: %d\n", write(s, &value, sizeof(int)));
            to--;
            if (to < 0) {
                to = TO;
                sleep(1);
            }
        } while(!feof(stdin));
        shutdown(s, SHUT_WR);
    }

    if( status < 0 ) perror("uh oh");

    close(s);
    return 0;
}
