What data structure do you expect to have the best (fastest) performance? Which one do you expect to be the slowest? Do the results of timing your program’s execution match your expectations? If so, briefly explain the correlation. If not, what run times deviated and briefly explain why you think this is the case.

I expect HashMap to be the fastest and BST and AVL trees to be around the same. AVL could be slightly slower than BST due to the additional balancing step for insertion. Below are the results of execution times.

Runtimes (seconds):

BST:
Trial 1: 0.767
Trial 2: 0.790
Trial 3: 0.750
Trial 4: 0.768 
Trial 5: 0.765

Average: 0.768

AVL:
Trial 1: 0.855
Trial 2: 0.845 
Trial 3: 0.718 
Trial 4: 0.772
Trial 5: 0.752 

Average: 0.788

Hash:
Trial 1: 0.651
Trial 2: 0.641
Trial 3: 0.584
Trial 4: 0.538
Trial 5: 0.526

Average: 0.588

The results matched my expectation. HashMap ran the fastest at an average of 0.558 seconds, while BST and AVL are slower at 0.768 seconds and 0.788, respectively. AVL took slightly longer than BST. The fast performance Hash is because insertion run time is amortized O(1) constant time with the very occasional O(n) when resizing is required. Furthermore, searching for an element in a hash map is also constant time theta(1). Traversing through the entire HashMap would take O(n).

By contrast, for BST, the average runtime for insertion is theta(lg n). In the worst case that everything in the tree is unbalanced on a single branch, the run times would be O(n). Traversing through the entire BST using the stack takes O(n).

For AVL tree, the average runtime for insertion is also theta(lg n), and the worst case is O(lg n). It has a better worst case than BST because the tree is balanced with a height of lg n. However, the rebalancing step adds a constant time operation after each insertion of a new word. Although the rotation step is constant time, it is plausible that the coefficient that it results in is greater than that of BST, contributing to the slightly slower run time. Traversing through the entire AVL tree using the stack takes O(n).



