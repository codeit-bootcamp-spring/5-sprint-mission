package com.sprint.mission.discodeit.log;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

public final class LogUtils {

	private LogUtils() {
	}

	public static String maskEmail(String email) {
		if (email == null) {
			return null;
		}
		int at = email.indexOf('@');
		if (at <= 1) {
			return "***";
		}
		return email.substring(0, 2) + "***" + email.substring(at);
	}

	public static String summarize(String s, int max) {
		if (s == null) {
			return null;
		}
		return s.length() > max ? s.substring(0, max) + "...(truncated)" : s;
	}

	public static String humanReadableSize(long size) {
		if (size < 1024) {
			return size + "B";
		}
		int z = (63 - Long.numberOfLeadingZeros(size)) / 10;

		return String.format("%.1f %sB", (double)size / (1L << (z * 10)), " KMGTPE".charAt(z));
	}

	public static String summarizeAttachments(List<BinaryContentDto> attachments, int limit) {
		if (attachments == null || attachments.isEmpty()) {
			return "[]";
		}

		String summary = attachments.stream()
			.limit(limit)
			.map(a -> String.format("%s/%s/%s",
				a.fileName(),
				a.contentType(),
				humanReadableSize(a.size())))
			.collect(Collectors.joining(", ", "[", "]"));

		int more = Math.max(0, attachments.size() - limit);
		if (more > 0) {
			summary += " (+" + more + ")";
		}

		return summary;
	}

	public static String summarizeAttachment(BinaryContentDto attachment) {
		return attachment == null
			? "[]"
			: summarizeAttachments(List.of(attachment), 1);
	}

	public static String summarizeMultipartFiles(List<MultipartFile> files, int limit) {
		if (files == null || files.isEmpty()) {
			return "[]";
		}

		String summary = files.stream()
			.limit(limit)
			.map(f -> String.format("%s/%s/%s",
				f.getOriginalFilename(),
				f.getContentType(),
				humanReadableSize(f.getSize())))
			.collect(Collectors.joining(", ", "[", "]"));

		int more = Math.max(0, files.size() - limit);
		if (more > 0) {
			summary += " (+" + more + " more)";
		}

		return summary;
	}

	public static String summarizeMultipartFile(MultipartFile file) {
		return file == null
			? "[]"
			: summarizeMultipartFiles(List.of(file), 1);
	}
}
