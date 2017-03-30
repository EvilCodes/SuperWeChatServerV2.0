package cn.ucai.superwechat.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.bean.Log;
import cn.ucai.superwechat.dao.ILogDao;
import cn.ucai.superwechat.dao.LogDaoImpl;

@WebServlet("/logs")
public class LogServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ILogDao dao = new LogDaoImpl();
	public LogServlet() {
		super();
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int sum = Integer.parseInt(req.getParameter("sum"));
		List<Log> list = dao.getLogs(sum);
		PrintWriter pw = resp.getWriter();
		pw.println("<html><body>");
		for(Log log : list){
			pw.println(log.toString()+"</br>");
		}
		pw.println("</body></html>");
		pw.close();
	}
}
