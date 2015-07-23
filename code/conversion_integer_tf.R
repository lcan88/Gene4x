net <- as.matrix(read.delim('./temp/net_tf_unique_nodes.txt', header = FALSE, sep = "\t"))
#print(dim(net))
conversion<-as.matrix(read.delim("./temp/nodes.txt", header = FALSE, sep = "\t"))
for (i in 1:dim(conversion)[1]){
    m<-match(net[,1],conversion[i,1])
    w<-which(!is.na(m))
    net[w,1]<-as.numeric(i)
    m1<-match(net[,2],conversion[i,1])
    w1<-which(!is.na(m1))
    net[w1,2]<-as.numeric(i)
    #print(i)
}
write.table(net,'./temp/net_tf_unique_nodes_int.txt',sep='\t',row.names=FALSE,col.names=FALSE)
