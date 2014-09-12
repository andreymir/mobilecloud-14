package org.magnum.mobilecloud.video;


import java.security.Principal;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller
public class VideoController {

	@Autowired
	private VideoRepository videos;
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v){
		 videos.save(v);
		 return v;
	}
	
	// Receives GET requests to /video and returns the current
	// list of videos in memory. Spring automatically converts
	// the list of videos to JSON because of the @ResponseBody
	// annotation.
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList(){
		return Lists.newArrayList(videos.findAll());
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") long id) {
		Video v = videos.findOne(id);
		
		if (v == null) {
			throw new ResourceNotFoundException();
		}
		
		return v;
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method=RequestMethod.POST)
	public void likeVideo(@PathVariable("id") long id, HttpServletResponse response, Principal p) {
		Video v = getVideoById(id);
		String userName = p.getName();
		
		if (v.getUsers().contains(userName)) {
			throw new LikeException();
		}
		
		v.getUsers().add(userName);
		v.setLikes(v.getLikes() + 1);
		
		videos.save(v);
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method=RequestMethod.POST)
	public void unlikeVideo(@PathVariable("id") long id, HttpServletResponse response, Principal p) {
		Video v = getVideoById(id);
		String userName = p.getName();
		
		if (!v.getUsers().contains(userName)) {
			throw new LikeException();
		}
		
		v.getUsers().remove(userName);
		v.setLikes(v.getLikes() - 1);
		
		videos.save(v);
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/likedby", method=RequestMethod.GET)
	public @ResponseBody Collection<String> getUsersWhoLikedVideo(@PathVariable("id") long id, HttpServletResponse response) {
		Video v = getVideoById(id);
		return v.getUsers();
	}
	
	// Receives GET requests to /video/find and returns all Videos
	// that have a title (e.g., Video.name) matching the "title" request
	// parameter value that is passed by the client
	@RequestMapping(value=VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(
			// Tell Spring to use the "title" parameter in the HTTP request's query
			// string as the value for the title method parameter
			@RequestParam(VideoSvcApi.TITLE_PARAMETER) String title
	){
		return videos.findByName(title);
	}
	
	
	@RequestMapping(value=VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(
			// Tell Spring to use the "title" parameter in the HTTP request's query
			// string as the value for the title method parameter
			@RequestParam(VideoSvcApi.DURATION_PARAMETER) long duration
	){
		return videos.findByDurationLessThan(duration);
	}
	
	
}
