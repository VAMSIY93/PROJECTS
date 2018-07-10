package main

import "fmt"
import "time"

func main() {
	jobs:=make(chan string, 2)
	
	//Read Input Strings
	/*reader := bufio.NewReader(os.Stdin)
	fmt.Print("Enter Number of Nodes: ")
	text, _ := reader.ReadString('\n')
	fmt.Println(text)*/

	/*jobs<-"got it"
	go func() {
		msg:=<-jobs
		fmt.Println(msg)
		jobs<-"sending it"
	}()*/

	list := make(map[string]string)

	list["hello"] = "world"
	list["hai"] = "dude"

	fmt.Println(list["hai"])
	fmt.Println(list["know"])

	if list["know"]=="" {
		fmt.Println("success")
	} else {
		fmt.Println("fail")
	}

	select {
		case msg:=<-jobs:
			fmt.Println("rec: ",msg)
		case <-time.After(time.Second*2):
			fmt.Println("Not yet recieved")
	}
}