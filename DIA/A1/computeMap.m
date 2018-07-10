function Map=computeMap(Map)
    start=1;
    while start<257 && Map(start,1)>0
        start=start+1;
    end
    finish=start+1;
    while finish<257
        count=0;
        if start==1
            count=1;
        end
        while finish<257 && Map(finish,1)==0 
            finish=finish+1;
            count=count+1;
        end
        half=uint8((count-1)/2);
        while start<finish && count>0            
            if (start>1 && count>half) || (start<257 && finish==257)
                Map(start,1)=Map(start-1,1);
                Map(start,2)=Map(start-1,2);
                Map(start,3)=Map(start-1,3);
                start=start+1;
                count=count-1;
            elseif (finish<257 && start<257 && count<=half) || (start==1)
                Map(start,1)=Map(finish,1);
                Map(start,2)=Map(finish,2);
                Map(start,3)=Map(finish,3);
                start=start+1;
                count=count-1;
            end
        end
        
        while finish<257 && Map(finish,1)~=0
            finish=finish+1;
        end
        start=finish;
    end
end