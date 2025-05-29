package com.example.webhookapp.service;

import org.springframework.stereotype.Service;

@Service
public class SqlSolutionService {
    
    public String getSqlSolution(String regNo) {
        // Extract last digit from regNo to determine odd/even
        String lastChar = regNo.substring(regNo.length() - 1);
        int lastDigit = Integer.parseInt(lastChar);
        
        if (lastDigit % 2 == 1) {
            // Odd - Question 1 solution
            return getSolutionForQuestion1();
        } else {
            // Even - Question 2 solution
            return getSolutionForQuestion2();
        }
    }
    
    private String getSolutionForQuestion1() {
        // TODO: Replace this with the actual SQL solution from Google Drive document
        // This is for ODD registration numbers (Question 1)
        return """
            SELECT 
                p.product_name,
                SUM(od.quantity * od.unit_price) as total_revenue
            FROM products p
            JOIN order_details od ON p.product_id = od.product_id
            JOIN orders o ON od.order_id = o.order_id
            WHERE o.order_date >= '2023-01-01'
            GROUP BY p.product_id, p.product_name
            ORDER BY total_revenue DESC
            LIMIT 10;
            """;
    }
    
    private String getSolutionForQuestion2() {
        // TODO: Replace this with the actual SQL solution from Google Drive document
        // This is for EVEN registration numbers (Question 2)
        return """
            SELECT 
                c.customer_name,
                COUNT(o.order_id) as order_count,
                AVG(o.total_amount) as avg_order_value
            FROM customers c
            LEFT JOIN orders o ON c.customer_id = o.customer_id
            WHERE o.order_date BETWEEN '2023-01-01' AND '2023-12-31'
            GROUP BY c.customer_id, c.customer_name
            HAVING COUNT(o.order_id) > 5
            ORDER BY order_count DESC;
            """;
    }
}
