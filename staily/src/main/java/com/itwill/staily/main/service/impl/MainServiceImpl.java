package com.itwill.staily.main.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itwill.staily.main.mapper.MainMapper;
import com.itwill.staily.main.service.MainService;
import com.itwill.staily.mypage.model.dto.Bookmark;
import com.itwill.staily.util.Member;
import com.itwill.staily.util.Product;
import com.itwill.staily.util.Work;

@Service
public class MainServiceImpl implements MainService{
	
	@Autowired
	private MainMapper mainMapper;
	
	public MainServiceImpl() {
	
	}
	
	public MainMapper getMainMapper() {
		return mainMapper;
	}

	public void setMainMapper(MainMapper mainMapper) {
		this.mainMapper = mainMapper;
	}

	@Override
	public List<Bookmark> selectByBookmark(int mNo) throws Exception {
		return mainMapper.selectByBookmark(mNo);
	}

	@Override
	public List<Product> selectByView() throws Exception {
		return mainMapper.selectByView();
	}

	@Override
	public List<Work> selectByCategory(String category) throws Exception {
		return mainMapper.selectByCategory(category);
	}

	@Override
	public Work selectByWork(int wNo) throws Exception {
		return mainMapper.selectByWork(wNo);
	}

	@Override
	public List<Work> selectByEpisode(Map map) throws Exception {
		return mainMapper.selectByEpisode(map);
	}

	@Override
	public int createBookmark(Map map) throws Exception {
		return mainMapper.createBookmark(map);
	}

	@Override
	public int deleteBookmark(int bmNo) throws Exception {
		return mainMapper.deleteBookmark(bmNo);
	}

	@Override
	public int selectTepisode(int wNo) {
		return mainMapper.selectTepisode(wNo);
	}

	@Override
	public void increaseWorkView(int wNo) {
		mainMapper.increaseWorkView(wNo);
	}

	@Override
	public void increaseProductView(int pNo) {
		mainMapper.increaseProductView(pNo);
	}

	@Override
	public int selectProductCount() {
		return mainMapper.selectProductCount();
	}

	@Override
	public List<Work> selectTodayofWork() {
		return mainMapper.selectTodayofWork();
	}
	
}