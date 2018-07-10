function [E,valid] = computeEnergy(E)
    [r,c]=size(E);
    valid=1;
    for i=2:r
        for j=1:c
            if E(i,j)>=0
                if j==1
                    [me,~,valid] = computeMin(E(i-1,j:j+1));
                    %{
                    me = A(1);
                    for k=1:2
                        if A(k)>=0
                            me = A(k);
                            break;
                        elseif k==2
                            valid=false;
                            return;
                        end
                    end
                    %}
                elseif j==c
                    [me,~,valid] = computeMin(E(i-1,j-1:j));
                    %{
                    me = A(1);
                    for k=1:2
                        if A(k)>=0
                            me = A(k);
                           break;
                        elseif k==2
                            valid=false;
                            return;
                        end
                    end
                    %}
                else
                    [me,~,valid] = computeMin(E(i-1,j-1:j+1));
                    %{
                    me = A(1);
                    for k=1:3
                        if A(k)>=0
                            me = A(k);
                            break;
                        elseif k==3
                            valid=false;
                            return;
                        end
                    end
                    %}
                end
                if valid==1
                    E(i,j) = E(i,j) + me;
                else
                    return;
                end
            end
        end
    end
end