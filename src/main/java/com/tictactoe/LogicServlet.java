package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession currentSession = req.getSession();

        Field field = extractField(currentSession);

        int indexCross = getSelectedIndex(req);

        if (field.getField().get(indexCross) == Sign.EMPTY) {
            field.getField().put(indexCross, Sign.CROSS);
        } else {
            req.getRequestDispatcher("/index.jsp").forward(req,resp);
            return;
        }

        if (checkWin(resp, currentSession, field)) return;

        int indexNought = field.getEmptyFieldIndex();
        if (indexNought >= 0) {
            field.getField().put(indexNought, Sign.NOUGHT);

            if (checkWin(resp, currentSession, field)) return;
        } else {
            currentSession.setAttribute("draw", true);

            List<Sign> data = field.getFieldData();

            currentSession.setAttribute("data", data);

            resp.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> dataField = field.getFieldData();

        currentSession.setAttribute("field", field);
        currentSession.setAttribute("fieldData", dataField);

        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : -1;
    }

    private Field extractField(HttpSession currentSession) {
        Object object = currentSession.getAttribute("field");
        if (object == null || object.getClass() != Field.class) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }

        return (Field) object;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            currentSession.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();

            currentSession.setAttribute("fieldData", data);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
