package org.smart.board.controller;

import org.smart.board.entity.Board;
import org.smart.board.service.BoardService;
import org.smart.board.util.PageNavigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/board")

public class BoardController {
    @Autowired
    private BoardService boardService;

    // 설정파일 읽어오기 :
    @Value("${spring.servlet.multipart.location}")
    private String uploadPath; // c:/upload

    /*
    * 게시글 요청
    * */
    @GetMapping("/boardList")
    public String boardList(@RequestParam(defaultValue = "1") int currentPage,
                            @RequestParam(defaultValue = "title") String searchItem,
                            @RequestParam(defaultValue = "") String searchWord,
                            Model model) {

        // DB에서 저장된 전체 글개수를 얻어옴
        int totalRecordCount = boardService.getBoardCount(searchItem, searchWord);
        PageNavigator navi = new PageNavigator(currentPage, totalRecordCount);
        int countPerPage = navi.getCountPerPage();

        int srow = 1 + (currentPage-1) * countPerPage;
        int erow = countPerPage + (currentPage-1) * countPerPage;


        List<Board> boardList = boardService.findAll(srow, erow, searchItem, searchWord);
        System.out.println(boardList);
        System.out.println("가져온 글 갯수 : " + boardList.size());

        model.addAttribute("boardList", boardList);
        model.addAttribute("searchItem", searchItem);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("navi", navi);
        model.addAttribute("srow", srow);
        model.addAttribute("erow", erow);

        return "board/boardList";
    }
    /*
    * 게시글 등록 화면 요청
    * @return
    * */

    @GetMapping("/boardWrite")
    public String boardWrite() {

        return "board/boardWrite";
    }

    /*
    * 파일을 첨부해서 올릴 때 그것까지 처리하는 메소드
    */
    // session
    @PostMapping("/boardWrite")
    public String boardWrite(Board board, MultipartFile attachFile, @AuthenticationPrincipal UserDetails user) {

        System.out.println(attachFile + ", " + attachFile.getName() + ", "+ attachFile.getSize() + ", " + attachFile.isEmpty());
        // 파일이 첨부됐다면
        if(!attachFile.isEmpty()) {
            // 업로드 폴더가 존재하는지 확인 : 없다면 생성
            File path = new File(uploadPath);
            if(path.isDirectory())
                path.mkdir();

            String originalfile = attachFile.getOriginalFilename();

            // savedfile을 만듦
            String uuid = UUID.randomUUID().toString();

            String filename;    // 확장자를 뺀 파일명
            String ext;         // 확장자
            String savedfile;

            int index = originalfile.indexOf('.');
            filename = originalfile.substring(0, index);

            // 확장자가 없는 경우
            if(index == -1) {
                ext= "";
            }
            // 확장자가 있는 경우
            else {
                ext = originalfile.substring(index+1);
            }

            savedfile = filename + "_" + uuid + "." + ext;
            // 저 위의 경로에 savedfile을 저장하고 or sa ==> board에 setting한다
            File serverFile = null; // 경로 + savedfile
            serverFile = new File(uploadPath + "/" + savedfile);

            // 파일 저장하기
            try {
                attachFile.transferTo(serverFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            board.setOriginalfile(originalfile);
            board.setSavedfile(savedfile);
        } // 파일이 업로드 됐을 때 할 작업 끝끝

        board.setUsrid(user.getUsername());
        boardService.insert(board);

        return "redirect:/board/boardList";
    }

    @GetMapping("/boardDetail")
    public String boardDetail(Long boardseq, Model model) {
        // 1) DB에서 boardseq에 해당하는 하나의 글을 질의해옴
        // 1-1) 조회수 증가해야함
        int hitcount = boardService.hitCount(boardseq);
        Board board = boardService.findOne(boardseq);



        // 2) Model에 저장


        model.addAttribute("board", board);


        // 3) view로 forward
        return "board/boardDetail";
    }

    @GetMapping("/boardUpdate") // a 태그는 Get방식
    public String boardUpdate(Long boardseq, Model model) {
        Board board = boardService.findOne(boardseq);  // DB로 바로 갈 수 없으니 서비스 > mapper 로 이동해서

        model.addAttribute("board", board);

        return "board/boardUpdate";
    }

    @PostMapping("/boardUpdate")
    public String boardUpdate(Board board) {
        System.out.println(board);

        board.setUsrid("aaa"); // 로그인이 완료된 후 걷어낼 코드
        boardService.update(board);

        return "redirect:/board/boardList";
    }

    @GetMapping("/boardDelete")
    public String boardDelete(Long boardseq) {
        boardService.delete(boardseq);

        return "redirect:/board/boardList";
    }
    // boardseq --> db --> savedfile --> c:/upload --> 클라이언트에게 전송(소켓통신)
    @GetMapping("/download")
    public String download(Long boardseq, HttpServletResponse response) {
        Board board = boardService.findOne(boardseq);

        // 파일명
        String originalfile = board.getOriginalfile(); // 사용자
        String savedfile    = board.getSavedfile(); // HDD
        // 실제 파일이 저장된 Full Path
        String fullPath = uploadPath + "/" + savedfile;

        try {
            response.setHeader("Content-Disposition", "attachment;filename="
                    + URLEncoder.encode(originalfile, "UTF-8"));
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //
        FileInputStream filein = null;
        ServletOutputStream fileout = null;

        try {
            filein = new FileInputStream(fullPath);
            fileout = response.getOutputStream();

            FileCopyUtils.copy(filein, fileout);

            fileout.close();
            filein.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
