package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Slf4j
@Controller
public class AritcleController {
    @Autowired //스프링 부트가 미리 생성해 놓은 레파지토리 객체 주입(DI-의존성 주입)
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form) {
        log.info(form.toString());

        // 1. DTO를 엔티티로 변환
        Article article = form.toEntity();
        log.info(article.toString()); //DTO가 entity로 잘 변환되는지

        // 2. 레파지터리로 엔티티를 DB에 저장
        Article saved = articleRepository.save(article);
        log.info(saved.toString()); //article이 DB에 잘 저장되는지

        return "redirect:/articles/" + saved.getId();
    }

    @GetMapping("/articles/{id}") //데이터 조회 요청 접수
    public String show(@PathVariable Long id, Model model) { //매개 변수로 id 받아오기
        log.info("id = " + id); //id 잘 받아오는지 확인하는 로그

        // 1. id를 조회해 데이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null); // id값으로 데이터를 조회한 결과, 값이 있으면 articleEntity 변수에 값을 넣고 없으면 null을 저장

        // 2. 모델에 데이터 등록하기
        model.addAttribute("article", articleEntity); // article 이라는 이름으로 articleEntity 객체를 등록

        // 3. 뷰 페이지 반환하기
        return "articles/show";
    }


    @GetMapping("/articles")
    public String index(Model model){
        // 1. DB에서 모든 Article 데이터 가져오기
        List<Article> articleEntityList = articleRepository.findAll(); // 방법 3을 사용하여 형변환 ===> 정확하게 하려면 List<Atricle> 을 ArrayList<Atricle>로 변경해야 하지만 ArrayList 의 상위 타입인 List로도 업캐스팅 할 수 있다.


        //findAll() 메서드가 반환되는 데이터 타입은 Iterable ===> 형변환이 필요하다

        // 형변환 방법 1.  List로 형변환 시키기 => Iterable<Article>을 List<Article>로 다운캐스팅
        // 1. List<Article> articleEntityList = (List<Article>) articleRepository.findAll();

        // 형변환 방법 2.  articleEntityList의 타입을 findAll() 메서드가 반환하는 타입으로 맞추기
        // 2. Iterable<Article> articldEntityList = articleRepository.findAll();

        // 형변환 방법 3.  ArrayList 이용하기 (CrudRepository의 메서드를 오버라이딩 해주기)
        // 3. ArticleRepository 블록 안에서 generate -> overide Methos 선택하고 findAll():Iterable<T> 선택 Iterable<Article>을 ArrayList<Article> 로 변경해주기



        // 2. 가져온 Article 묶음을 모델에 등록하기
        model.addAttribute("articleList", articleEntityList); //articleEntityList 등록


        // 3. 사용자에게 보여 줄 뷰 페이지 설정하기
        return "articles/index";
    }


    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model){
        // DB에서 수정할 데이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null);

        // 모델에 데이터 등록하기
        model.addAttribute("article", articleEntity);

        return "articles/edit";
    }


    @PostMapping("/articles/update")
    public String update(ArticleForm form){
        log.info(form.toString());

        // 1. DTO를 엔티티로 변환하기
        Article articleEntity = form.toEntity(); // DTO(form)를 엔티티로 변환
        log.info(articleEntity.toString());

        // 2. 엔티티를 DB에 저장하기
        // 2-1. DB에서 기존 데이터 가져오기
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null); //DB에서 데이터 찾아서 target으로 데이터 저장하고 없으면 null 값 반환
        //2-2. 기존 데이터 값을 갱신하기
        if (target != null) {
            articleRepository.save(articleEntity); //엔티티를 DB에 저장(갱신)
        }


        // 3. 수정 결과 페이지로 리다이렉트하기
        return "redirect:/articles/" + articleEntity.getId();
    }


    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr){
        log.info("삭제 요청이 들어왔습니다!!");

        // 1. 삭제할 대상 가져오기
        Article target = articleRepository.findById(id).orElse(null);
        log.info(target.toString());

        // 2. 대상 엔티티 삭제하기
        if (target != null) {
            articleRepository.delete(target);
            rttr.addFlashAttribute("msg", "삭제되었습니다!");
        }

        // 3. 결과 페이지로 리다이렉트하기
        return "redirect:/articles";
    }


}
